/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.ysm.client.animation.molang.CustomMolangParser
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package io.github.tt432.yessteveskill.yss.molang;

import com.elfmcys.ysm.client.animation.molang.CustomMolangParser;
import io.github.tt432.yessteveskill.yss.molang.YssMolangBinding;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class YssMolangRegister {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"YesSteveSkill/Molang");
    private static volatile boolean registered;

    private YssMolangRegister() {
    }

    public static void register() {
        try {
            Field extraField = CustomMolangParser.class.getDeclaredField("EXTRA_BINDING");
            extraField.setAccessible(true);
            Map extraBinding = (Map)extraField.get(null);
            if (extraBinding.isEmpty()) {
                CustomMolangParser.rentInstance();
            }
            extraBinding.put("yss", new YssMolangBinding());
            Field poolField = CustomMolangParser.class.getDeclaredField("PARSER_POOL");
            poolField.setAccessible(true);
            ((Collection)poolField.get(null)).clear();
            registered = true;
            LOGGER.info("yss molang \u7ed1\u5b9a\u5df2\u6ce8\u518c\uff1ayss.get_key / yss.is_key_down / yss.is_key_up");
        }
        catch (Exception e) {
            LOGGER.error("\u6ce8\u518c yss molang \u7ed1\u5b9a\u5931\u8d25\uff08YSM CustomMolangParser \u5185\u90e8\u7ed3\u6784\u53ef\u80fd\u5df2\u53d8\u5316\uff09\uff0cyss.* \u67e5\u8be2\u4e0d\u53ef\u7528", (Throwable)e);
        }
    }

    public static boolean isRegistered() {
        return registered;
    }
}

