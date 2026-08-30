package com.emfcompat.emotecraft;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(EmotecraftEmfCompat.MOD_ID)
public class EmotecraftEmfCompat {
    public static final String MOD_ID = "emf_compat_emotecraft";

    public EmotecraftEmfCompat() {
        // Запускаем клиентскую инициализацию только на стороне клиента
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        });
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        // Логика клиентской настройки
    }
}