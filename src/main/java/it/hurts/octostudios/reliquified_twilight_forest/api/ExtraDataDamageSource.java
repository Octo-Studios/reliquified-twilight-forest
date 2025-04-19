package it.hurts.octostudios.reliquified_twilight_forest.api;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ExtraDataDamageSource extends DamageSource {
    private Object[] extra;

    public Object[] getExtra() {
        return extra;
    }

    public ExtraDataDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 damageSourcePosition, Object... extra) {
        super(type, directEntity, causingEntity, damageSourcePosition);
        this.extra = extra;
    }

    public ExtraDataDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity, Object... extra) {
        super(type, directEntity, causingEntity);
        this.extra = extra;
    }

    public ExtraDataDamageSource(Holder<DamageType> type, Vec3 damageSourcePosition, Object... extra) {
        super(type, damageSourcePosition);
        this.extra = extra;
    }

    public ExtraDataDamageSource(Holder<DamageType> type, @Nullable Entity entity, Object... extra) {
        super(type, entity);
        this.extra = extra;
    }

    public ExtraDataDamageSource(Holder<DamageType> type, Object... extra) {
        super(type);
        this.extra = extra;
    }
}
