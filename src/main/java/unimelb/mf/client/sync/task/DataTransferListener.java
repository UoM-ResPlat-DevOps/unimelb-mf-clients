package unimelb.mf.client.sync.task;

public interface DataTransferListener<S, D> {

    void transferStarted(S src, D dst);

    void transferFailed(S src, D dst);

    void transferCompleted(S src, D dst);
    
    void transferSkipped(S src, D dst);

    void transferProgressed(S src, D dst, long increment);

}
