package net.flytre.fguns.gun;

public class Pistol extends AbstractGun {


    private Pistol(Builder builder) {
        super(builder);
    }


    public static class Builder extends AbstractGun.Builder<Builder> {

        public Builder() {
            super();
            scope = false;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public Pistol build() {
            return new Pistol(this);
        }
    }
}
