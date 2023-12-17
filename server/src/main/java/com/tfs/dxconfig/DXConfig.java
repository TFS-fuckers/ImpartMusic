package com.tfs.dxconfig;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;

public class DXConfig<T extends Config>{
    private final Class<T> type;
    private final File configFile;
    private final MessageRedirector redirector;
    private JsonObject jsonObject;
    private boolean valid = false;
    private BufferedReader reader = null;
    public DXConfig(Class<T> type, String jsonFilePath, MessageRedirector redirector) {
        this.redirector = redirector == null ? System.out::println : redirector;
        this.configFile = new File(jsonFilePath);
        this.type = type;
        if(!this.configFile.exists()) {
            this.log("Config file %s couldn't be found, generating empty config", this.configFile.getName());
            try{
                this.forceCreate();
                this.valid = true;
            }catch (IOException e) {
                this.valid = false;
            }
        } else {
            this.valid = true;
        }
        if(this.valid) {
            try {
                this.reader = new BufferedReader(new FileReader(configFile));
                this.readAll();
            } catch (IOException e) {
                this.log("Error when trying to create file reader and writer");
                this.log(e.getMessage());
                this.valid = false;
            }
        }
    }
    public DXConfig(Class<T> type, String jsonFilePath) {
        this(type, jsonFilePath, null);
    }
    private void log(String message) {
        this.redirector.log(message);
    }
    private void log(String format, Object... args) {
        this.redirector.log(String.format(format, args));
    }
    private void forceCreate() throws IOException {
        File parent = this.configFile.getParentFile();
        boolean parentSuccess;
        if(!parent.exists()) {
            parentSuccess = parent.mkdirs();
        } else {
            parentSuccess = true;
        }
        if(!parentSuccess) {
            throw new IOException("File creating failed: parent");
        }

        boolean childSuccess;
        try {
            childSuccess = this.configFile.createNewFile();
        } catch (IOException e) {
            this.redirector.log("Error while creating config file");
            e.printStackTrace();
            childSuccess = false;
        }
        if(!childSuccess) {
            throw new IOException("File creating failed: target");
        }
        this.writeDefault();
    }
    public boolean isValid() {
        return valid;
    }

    public void close() {
        try {
            this.reader.close();
        } catch (IOException e) {
            this.log("Error when trying to close config reader: %s", e.getMessage());
        } finally {
            this.valid = false;
        }
    }

    private void readAll() throws IOException {
        StringBuilder builder = new StringBuilder();
        String line = this.reader.readLine();
        builder.append(line);
        try {
            this.jsonObject = JsonParser.parseString(builder.toString()).getAsJsonObject();
        } catch (IllegalStateException e) {
            this.log("invalid config json, creating default");
            String defaultJson = this.writeDefault();
            if(defaultJson == null) {
                this.log("Error when writing default, config is corrupting");
                this.close();
                return;
            }
            this.jsonObject = JsonParser.parseString(defaultJson).getAsJsonObject();
        }
    }
    
    public JsonElement getValue(String key) {
        if(!this.isValid()) {
            this.log("You are trying to fetch an invalid config reader, please fix this first or the program might run wrongly");
            return null;
        }
        return this.jsonObject.get(key);
    }

    private String writeDefault() {
        try {
            Gson gson = new Gson();
            String content = gson.toJson(this.type.newInstance());
            this.write(content, false);
            return content;
        } catch (Exception e) {
            this.log("Error when trying to write default config");
            this.valid = false;
            this.close();
        }
        return null;
    }

    private void write(String content, boolean append) {
        try {
            FileWriter writer = new FileWriter(this.configFile, append);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            this.log("Error when writing json");
            this.valid = false;
            this.close();
        }
    }
}