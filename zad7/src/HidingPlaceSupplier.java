public interface HidingPlaceSupplier {

    public interface HidingPlace {
        public boolean isPresent();
        public double openAndGetValue();
    }
    public HidingPlace get();
    public int threads();
}