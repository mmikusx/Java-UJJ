public class ParallelSearcher implements ParallelSearcherInterface{
    protected double sumOfValues;

    @Override
    public void set(HidingPlaceSupplierSupplier supplier) {
        HidingPlaceSupplier setOfLockers = supplier.get(0);

        while(setOfLockers != null){
            Thread[] threads = new Thread[setOfLockers.threads()];

            for(int i=0; i < threads.length; i++){
                searchLocker search = new searchLocker(setOfLockers, getBlock());
                threads[i] = search;
                threads[i].start();
            }

            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            setOfLockers = supplier.get(sumOfValues);
            sumOfValues = 0;
        }
    }

    private ParallelSearcher getBlock() {
        return this;
    }

    private class searchLocker extends Thread{
        HidingPlaceSupplier setOfLockers;
        final Object block;

        public searchLocker(HidingPlaceSupplier setOfLockerss, Object blockk) {
            setOfLockers = setOfLockerss;
            block = blockk;
        }

        public void run(){
            HidingPlaceSupplier.HidingPlace locker;
            locker = setOfLockers.get();
            while(locker != null){
                if(locker.isPresent()){
                    synchronized (block){
                        sumOfValues += locker.openAndGetValue();
                    }
                }
                locker = setOfLockers.get();
            }
        }
    }
}