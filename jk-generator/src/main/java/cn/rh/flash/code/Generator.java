package cn.rh.flash.code;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.lang.Files;
import org.nutz.lang.Mirror;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 入口类<br>
 */
public class Generator {
    private static final Log log = Logs.get();
    private final TableDescriptor table;

    public Generator(Map<String, TableDescriptor> tables, TableDescriptor table) {
        this.table = table;
    }

    public void generate(String packageName, String templatePath, File file, boolean force)
            throws IOException {
        if (file.exists() && !force) {
            log.debug("file " + file + " exists, skipped");
            return;
        }

        String code = generateCode(packageName, templatePath);
        file.getParentFile().mkdirs();
        Files.write(file, code.getBytes(Charset.forName("utf8")));

    }

    public String generateCode(String packageName, String templatePath) throws IOException {
        VelocityContext context = new VelocityContext();
        context.put("table", table);
        context.put("packageName", packageName);
        StringWriter writer = new StringWriter();

        String template = new String(Streams.readBytes(ClassLoader.getSystemResourceAsStream(templatePath)),
                Charset.forName("utf8"));
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty("runtime.references.strict", false);
        engine.init();
        engine.evaluate(context, writer, "generator", template);
        return writer.toString();

    }

    static Pattern includePattern = Pattern.compile("DzRedEnvelopeVipMessage");
   // static Pattern includePattern = Pattern.compile("TaskOrder");
    public static void main(String[] args) throws Exception {

        String module = "dzvip"; //指定当前的模块
            String outputDir = "src/main/java";//输入路径
        boolean force = false; //是否覆盖

        String configPath = "code/code.json";
        String basePath = "";
        String baseUri = "/";
        //父级包名
        String basePackageName = "cn.rh.flash";
        //控制器
        String controllerPackageName = "api.controller.dz." + module;
        //业务bean
        String servicePackageName = "service." + module;
        //数据dao
        String repositoryPackageName = "dao." + module;
        //talbe entity
        String modelPackageName = "bean.entity." + module;

        String[] types = {"all"};
        String[] pages = {"index", "add", "edit", "detail"};

        //加载bean
        JsonLoader jsonLoader = new JsonLoader(configPath );
        //成Ioc容器 Aop的连接点
        Ioc ioc = new NutIoc( jsonLoader );
        CodeConfig codeConfig = ioc.get(CodeConfig.class);

        log.debug("=================================================");

        AbstractLoader loader = Mirror.me( EntityDescLoader.class ).born();
        Map<String, TableDescriptor> tables = loader.loadTables(ioc,
                basePackageName,
                baseUri,
                servicePackageName,
                repositoryPackageName,
                modelPackageName);
        for (Map.Entry<String, TableDescriptor> entry : tables.entrySet()) {
            if (includePattern != null) {
                String className = entry.getValue().getEntityClassName();
                if (!includePattern.matcher(className).find()) {
                    log.debug("跳过 " + className);
                    continue;
                }
            }

            String tableName = entry.getKey();

            log.debug("创建 " + tableName + " ...");
            TableDescriptor table = entry.getValue();
            Generator generator = new Generator(tables, table);
            Map<String, String> typeMap = new HashMap<String, String>(10);
            typeMap.put("model", modelPackageName);
            typeMap.put("service", servicePackageName);
            typeMap.put("controller", controllerPackageName);
            typeMap.put("repository", repositoryPackageName);

            for (String type : new String[]{"model", "repository", "service", "controller", "view"}) {
                if (!isTypeMatch(types, type)) {
                    continue;
                }
                if ("view".equals(type)) {
                    generateViews(basePath, codeConfig, force, table, generator, pages);
                } else {
                    if (loader instanceof EntityDescLoader && "model".equals(type)) {
                        continue;
                    }
                    String packageName = basePackageName + "." + typeMap.get(type);
                    String templatePath = "code/" + type + ".vm";

                    String packagePath = packageName.replace('.', '/');
                    String className = table.getEntityClassName();
                    if (!"model".equals(type)) {
                        className = Utils.upperCamel(className) + Strings.upperFirst(type);
                    }
                    File file = new File(basePath + codeConfig.getModel(type) + File.separator + outputDir, packagePath + "/" + className + ".java");
                    log.debug("generate " + file.getName());
                    generator.generate(packageName, templatePath, file, force);
                }
            }
        }
        ioc.depose();
        log.debug("结束!");
    }
    private static boolean isTypeMatch(String[] types, String type) {
        for (String t : types) {
            if (t.equalsIgnoreCase(type) || "all".equalsIgnoreCase(t)) {
                return true;
            }
        }
        return false;
    }
    private static void generateViews(String basePath, CodeConfig codeConfig, boolean force,  TableDescriptor table,  Generator generator, String[] pages) throws IOException {
        //生成vue版本相关文件
        File apiFile = new File(basePath + codeConfig.getViewModel() + "/src/api/" + table.getLastPackageName() + File.separator + table.getEntityNameLowerFirstChar() + ".js");
        generator.generate(null, "code/view/api.js.vm", apiFile, force);

        File vueFile = new File(basePath + codeConfig.getViewModel() + "/src/views/" + table.getLastPackageName() + File.separator + table.getEntityNameLowerFirstChar() + File.separator + "index.vue");
        generator.generate(null, "code/view/index.vue.vm", vueFile, force);

        File jsFile = new File(basePath + codeConfig.getViewModel() + "/src/views/" + table.getLastPackageName() + File.separator + table.getEntityNameLowerFirstChar() + File.separator + table.getEntityNameLowerFirstChar() + ".js");
        generator.generate(null, "code/view/index.js.vm", jsFile, force);

    }

}
