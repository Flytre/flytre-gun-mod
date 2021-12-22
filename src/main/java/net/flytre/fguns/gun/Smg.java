package net.flytre.fguns.gun;


import net.flytre.fguns.misc.Sounds;

public class Smg extends AbstractGun {

    private Smg(Builder builder) {
        super(builder);
    }

    public static class Builder extends AbstractGun.Builder<Builder> {

        public Builder() {
            super();
            this.fireSound = Sounds.RIFLE_FIRE_EVENT;
            this.scope = false;
            this.horizontalRecoil = 0.8;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public Smg build() {
            return new Smg(this);
        }
    }
}
