package unimelb.mf.client.util;

public interface HasProgress {

    long totalOperations();

    long completedOperations();

    String currentOperation();

}
