package meridian.intellij.findings;

import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.io.File;
import java.util.List;

public final class MeridianFindingsPanel extends JPanel {
    private final Project project;
    private final DefaultTableModel model;

    public MeridianFindingsPanel(Project project) {
        super(new BorderLayout());
        this.project = project;

        this.model = new DefaultTableModel(
                new String[]{"Severity", "Type", "File", "Line", "Title", "Confidence"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JBTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                navigateToSelectedFinding(table);
            }
        });

        JButton refreshButton = new JButton("Refresh Findings View");
        refreshButton.addActionListener(event -> refresh());

        JButton clearButton = new JButton("Clear Findings");
        clearButton.addActionListener(event -> {
            project.getService(MeridianFindingsState.class).clearFindings();
            refresh();
        });

        JPanel buttons = new JPanel();
        buttons.add(refreshButton);
        buttons.add(clearButton);

        add(buttons, BorderLayout.NORTH);
        add(new JBScrollPane(table), BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        SwingUtilities.invokeLater(() -> {
            model.setRowCount(0);

            List<MeridianFinding> findings = project.getService(MeridianFindingsState.class).getFindings();
            for (MeridianFinding finding : findings) {
                model.addRow(new Object[]{
                        finding.severity(),
                        finding.type(),
                        finding.file(),
                        finding.line(),
                        finding.title(),
                        finding.confidence()
                });
            }
        });
    }

    private void navigateToSelectedFinding(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }

        String filePath = String.valueOf(model.getValueAt(row, 2));
        int line = Integer.parseInt(String.valueOf(model.getValueAt(row, 3)));

        File file = new File(project.getBasePath(), filePath);
        var virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
        if (virtualFile == null) {
            return;
        }

        new OpenFileDescriptor(project, virtualFile, Math.max(0, line - 1), 0).navigate(true);
    }
}