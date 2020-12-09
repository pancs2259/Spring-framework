package com.pancs.config;

import java.io.File;
import java.net.URL;

public class ApplicationContext {

    private Class configClazz;

    public ApplicationContext(Class configClazz) {
        this.configClazz = configClazz;

        //扫描
        if (configClazz.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScanAnnotation = (ComponentScan) configClazz.getAnnotation(ComponentScan.class);
            String path = componentScanAnnotation.value();
            path = path.replace(".", "/");
            ClassLoader classLoader = ApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);
            File fileList = new File(resource.getFile());
            File[] files = fileList.listFiles();
            for (File f : files){
                String s = f.getAbsolutePath();
                if(s.endsWith(".class")){
                    s = s.substring(s.indexOf("com"),s.indexOf(".class"));
                    s = s.replace("\\",".");
                    try {
                        Class clazz = classLoader.loadClass(s);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }



        }

    }

    public Object getBean(String beanName){
        return null;
    }

}
