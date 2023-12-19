package com.tfs.modloader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;

import com.tfs.datapack.Datapack;
import com.tfs.logger.Logger;
import com.tfs.server.Server;
import com.tfs.server.ServerHandler;

public class ModLoader {
    private ModLoader() {}
    private static final List<Mod> MODS = new LinkedList<Mod>();

    public static void load() {
        Logger.logInfo("Loading mods...");
        File modsFile = new File("./mods");
        if(!modsFile.exists()) {
            modsFile.mkdir();
        }
        File[] children = modsFile.listFiles();
        for(File modFile : children) {
            try {
                URLClassLoader loader = new URLClassLoader(new URL[]{modFile.toURI().toURL()});
                Class<?> modInitClass = loader.loadClass("ModInit");
                Method listener = modInitClass.getMethod("onResolveDatapack", Datapack.class, Server.class, ServerHandler.class);
                Mod mod = new Mod(modFile.getName(), modInitClass.newInstance(), listener);
                MODS.add(mod);
                loader.close();
            } catch (MalformedURLException e) {
                Logger.logError("Error loading mods file %s, skipping", modFile.getName());
            } catch (ClassNotFoundException e) {
                Logger.logError("Could not load mods file %s because there's no ModInit class", modFile.getName());
            } catch (IllegalAccessException e) {
                Logger.logError("Illegal access occurred when loading mod %s", modFile.getName());
            } catch (InstantiationException e) {
                Logger.logError("Couldn't initialize mod instance of mod %s!", modFile.getName());
            } catch (IOException e) {
                Logger.logError("IO exception occurred when loading mod %s", modFile.getName());
            } catch (NoSuchMethodException e) {
                Logger.logError("Couldn't find onResolveDatapack() method in mod %s", modFile.getName());
            }
        }

        Logger.logInfo("Successfully loaded %d mods", MODS.size());
        for(Mod mod : MODS) {
            Logger.logInfo("MOD: %s", mod.name);
        }
    }

    public static void onResolveDatapack(Datapack datapack) {
        for(Mod mod : MODS) {
            mod.invoke(datapack);
        }
    }

    private static class Mod {
        private String name;
        private Object instance;
        private Method listener;

        public Mod(String name, Object instance, Method listener) {
            this.name = name;
            this.instance = instance;
            this.listener = listener;
        }

        public void invoke(Datapack pack) {
            try {
                this.listener.invoke(
                    this.instance,
                    pack, Server.INSTANCE(), ServerHandler.instance()
                );
            } catch (Exception e) {
                Logger.logError("Error when invoking mod instance %s", this.name);
                Logger.logError(e.getMessage());
            }
        }
    }
}