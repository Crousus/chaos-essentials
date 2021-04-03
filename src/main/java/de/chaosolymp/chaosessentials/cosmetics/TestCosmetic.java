package de.chaosolymp.chaosessentials.cosmetics;

import org.bukkit.Location;
import sv.file14.procosmetics.Core;
import sv.file14.procosmetics.cosmetic.particleffect.AbstractParticleEffect;
import sv.file14.procosmetics.cosmetic.particleffect.AbstractParticleEffectType;
import sv.file14.procosmetics.user.User;
import sv.file14.procosmetics.util.particle.ParticleType;
import sv.file14.procosmetics.util.particle.Particles;

public class TestCosmetic extends AbstractParticleEffect {

    private  int loop = 1;

    public static final AbstractParticleEffectType FLAMY = new AbstractParticleEffectType("flamy", TestCosmetic.class);

    public TestCosmetic(Core core, User user, AbstractParticleEffectType abstractParticleEffectType) {
        super(core, user, abstractParticleEffectType);
    }

    @Override
    public void onUpdate() {
        Location location = this.f.getLocation();
        location = location.add(0,2.3,0);

        if (isUserMoving()) {
            //Particles.display(ParticleType.FLAME, location.add(new Vector(0.1D, 0.9D, 0.1D)), 0.2F, 20);
        } else {
            loop = loop >= 40 ? 1 : loop;
                location = location.add(Math.sin(360 / (360 / (loop))) * 2,0,Math.cos(360 / (360 / (loop))) * 2);
                Particles.displayDirectional(ParticleType.HEART, location,0.1,0,0.5,0.1,200);
                System.out.println("  "+loop +" loc: "+location.getX()+"  "+location.getZ());
            loop++;
        }
    }

    public static void register(){
        System.out.println("registering flamy");
    }
}
