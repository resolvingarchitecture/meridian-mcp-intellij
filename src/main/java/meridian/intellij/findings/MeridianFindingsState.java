package meridian.intellij.findings;

import com.intellij.openapi.components.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service(Service.Level.PROJECT)
public final class MeridianFindingsState {
    private final List<MeridianFinding> findings = new ArrayList<>();
    private String status = "Idle";
    private String lastError = "";

    public synchronized List<MeridianFinding> getFindings() {
        return Collections.unmodifiableList(new ArrayList<>(findings));
    }

    public synchronized void replaceFindings(List<MeridianFinding> nextFindings) {
        findings.clear();
        findings.addAll(nextFindings);
        lastError = "";
    }

    public synchronized void clearFindings() {
        findings.clear();
        status = "Idle";
        lastError = "";
    }

    public synchronized String getStatus() {
        return status;
    }

    public synchronized void setStatus(String status) {
        this.status = status == null ? "Idle" : status;
    }

    public synchronized String getLastError() {
        return lastError;
    }

    public synchronized void setLastError(String lastError) {
        this.lastError = lastError == null ? "" : lastError;
    }
}