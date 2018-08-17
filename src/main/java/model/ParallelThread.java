package model;

public class ParallelThread extends Thread{
    private int _parallelId;
    private Runnable _parallelRunnable;

    public ParallelThread(int id) {
        _parallelId = id;
    }

    public int getParallelId() {
        return _parallelId;
    }

    public void setRunnable(Runnable runnable){
        _parallelRunnable = runnable;
    }

    @Override
    public void run(){
        _parallelRunnable.run();
    }
}
