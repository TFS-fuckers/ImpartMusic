package com.tfs.dxconfig;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;

/**
 * 标准配置文件阅读器
 */
public class DXConfig<T extends Config>{
    private final Class<T> type;
    private JsonObject defaultVal;
    private final File configFile;
    private final MessageRedirector redirector;
    private JsonObject jsonObject;
    private boolean valid = false;
    private BufferedReader reader = null;
    /**
     * 生成标准配置文件阅读器
     * @param type 目标结构
     * @param jsonFilePath 生成路径
     * @param redirector 信息重定向
     */
    public DXConfig(Class<T> type, String jsonFilePath, MessageRedirector redirector) {
        try {
            T instance = type.newInstance();
            Gson gson = new Gson();
            this.defaultVal = JsonParser.parseString(gson.toJson(instance)).getAsJsonObject();
        } catch (InstantiationException e) {
            this.log("Failed to instantiate config class: %s", type.getName());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if(this.defaultVal == null) {
            this.log("default config initialization failed, this might make program unstable!");
        }

        this.redirector = redirector == null ? System.out::println : redirector;
        this.configFile = new File(jsonFilePath);
        this.type = type;
        if(!this.configFile.exists()) {
            this.log("Config file %s couldn't be found, generating empty config");
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

    /**
     * 不使用重定向（默认sout）生成配置文件阅读器
     * @param type 目标结构
     * @param jsonFilePath 生成路径
     */
    public DXConfig(Class<T> type, String jsonFilePath) {
        this(type, jsonFilePath, null);
    }

    /**
     * 发送日志信息
     * @param message 内容
     */
    private void log(String message) {
        this.redirector.log(message);
    }

    /**
     * 格式化发送日志信息
     * @param format 格式化字符串
     * @param args 参数
     */
    private void log(String format, Object... args) {
        this.redirector.log(String.format(format, args));
    }

    /**
     * 强行创建新的配置文件
     * @throws IOException
     */
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

    /**
     * 获取该阅读器是否可用
     * @return 是否可用
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * 关闭此阅读器
     */
    public void close() {
        try {
            this.reader.close();
        } catch (IOException e) {
            this.log("Error when trying to close config reader: %s", e.getMessage());
        } finally {
            this.valid = false;
        }
    }

    /**
     * 阅读整个配置文件
     * @throws IOException
     */
    private void readAll() throws IOException {
        StringBuilder builder = new StringBuilder();
        while(true) {
            String line = this.reader.readLine();
            if(line == null) {
                break;
            }
            builder.append(line);
        }
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
    
    /**
     * 获取某个键的对应值
     * @param key 键
     * @return 对应值
     */
    public JsonElement getValue(String key) {
        if(!this.isValid()) {
            this.log("You are trying to fetch an invalid config reader, please fix this first or the program might run wrongly");
            return null;
        }
        JsonElement element = this.jsonObject.get(key);
        return element == null ? this.defaultVal.get(key) : element;
    }

    /**
     * 使用默认配置覆盖配置文件
     * @return 默认Json
     */
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

    /**
     * 写入配置文件
     * @param content 内容
     * @param append 是否在末尾继续
     */
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
    
    /**
     * 获取默认的配置类
     * @return 默认配置类
     */
    public T getDefault() {
        Gson gson = new Gson();
        return gson.fromJson(this.jsonObject.toString(), this.type);
    }

    /**
     * 将配置文件类保存
     * @param object 配置文件类生成的JsonObject
     */
    public void save(JsonObject object) {
        this.write(object.toString(), false);
        this.jsonObject = object;
    }
}