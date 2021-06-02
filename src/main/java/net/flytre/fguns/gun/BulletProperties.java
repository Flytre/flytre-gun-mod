package net.flytre.fguns.gun;

import com.google.gson.annotations.SerializedName;

public enum BulletProperties {

    @SerializedName("none")
    NONE(),

    @SerializedName("sniper")
    SNIPER(),

    @SerializedName("slime")
    SLIME(),

    @SerializedName("rocket")
    ROCKET(),

    @SerializedName("shocker")
    SHOCKER(),

    @SerializedName("flare")
    FLARE();

    BulletProperties() {

    }
}
