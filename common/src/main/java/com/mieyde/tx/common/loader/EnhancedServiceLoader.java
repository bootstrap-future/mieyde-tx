package com.mieyde.tx.common.loader;

import com.mieyde.tx.common.constants.Constants;
import com.mieyde.tx.common.exception.EnhancedServiceNotFoundException;
import com.mieyde.tx.common.executor.Initialize;
import com.mieyde.tx.common.util.CollectionUtils;
import com.mieyde.tx.common.util.ObjectUtils;
import com.mieyde.tx.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author 我吃稀饭面
 * @date 2023/6/27 13:31
 */
public class EnhancedServiceLoader {

    private static final ConcurrentMap<Class<?>, InnerEnhancedServiceLoader<?>> SERVICE_LOADERS = new ConcurrentHashMap<>();

    public static <T> T load(Class<T> service, ClassLoader loader) throws EnhancedServiceNotFoundException {
        return InnerEnhancedServiceLoader.getServiceLoader(service).load(loader);
    }

    public static <T> T load(Class<T> service) throws EnhancedServiceNotFoundException {
        return InnerEnhancedServiceLoader.getServiceLoader(service).load(findClassLoader());
    }

    public static <T> T load(Class<T> service, String activateName) throws EnhancedServiceNotFoundException {
        return InnerEnhancedServiceLoader.getServiceLoader(service).load(activateName, findClassLoader());
    }

    public static <T> T load(Class<T> service, String activateName, ClassLoader loader)
            throws EnhancedServiceNotFoundException {
        return InnerEnhancedServiceLoader.getServiceLoader(service).load(activateName, loader);
    }

    public static <T> T load(Class<T> service, String activateName, Object[] args)
            throws EnhancedServiceNotFoundException {
        return InnerEnhancedServiceLoader.getServiceLoader(service).load(activateName, args, findClassLoader());
    }

    public static <T> T load(Class<T> service, String activateName, Class<?>[] argsType, Object[] args)
            throws EnhancedServiceNotFoundException {
        return InnerEnhancedServiceLoader.getServiceLoader(service).load(activateName, argsType, args, findClassLoader());
    }

    public static <T> List<T> loadAll(Class<T> service) {
        return InnerEnhancedServiceLoader.getServiceLoader(service).loadAll(findClassLoader());
    }

    public static <T> List<T> loadAll(Class<T> service, Class<?>[] argsType, Object[] args) {
        return InnerEnhancedServiceLoader.getServiceLoader(service).loadAll(argsType, args, findClassLoader());
    }

    public static void unloadAll() {
        InnerEnhancedServiceLoader.removeAllServiceLoader();
    }

    public static <T> void unload(Class<T> service) {
        InnerEnhancedServiceLoader.removeServiceLoader(service);
    }

    public static <T> void unload(Class<T> service, String activateName) {

        if (activateName == null) {
            throw new IllegalArgumentException("activateName is null");
        }
        InnerEnhancedServiceLoader<T> serviceLoader = InnerEnhancedServiceLoader.getServiceLoader(service);
        ConcurrentMap<Class<?>, ExtensionDefinition<T>> classToDefinitionMap = serviceLoader.classToDefinitionMap;
        List<ExtensionDefinition<T>> extensionDefinitions = new ArrayList<>();
        for (Map.Entry<Class<?>, ExtensionDefinition<T>> entry : classToDefinitionMap.entrySet()) {
            String name = entry.getValue().getName();
            if (null == name) {
                continue;
            }
            if (name.equals(activateName)) {
                extensionDefinitions.add(entry.getValue());
                classToDefinitionMap.remove(entry.getKey());
            }
        }
        serviceLoader.nameToDefinitionsMap.remove(activateName);
        if (CollectionUtils.isNotEmpty(extensionDefinitions)) {
            for (ExtensionDefinition<T> definition : extensionDefinitions) {
                serviceLoader.definitionToInstanceMap.remove(definition);

            }
        }

    }

    public static <T> List<Class<T>> getAllExtensionClass(Class<T> service) {
        return InnerEnhancedServiceLoader.getServiceLoader(service).getAllExtensionClass(findClassLoader());
    }

    public static <T> List<Class<T>> getAllExtensionClass(Class<T> service, ClassLoader loader) {
        return InnerEnhancedServiceLoader.getServiceLoader(service).getAllExtensionClass(loader);
    }

    private static ClassLoader findClassLoader() {
        return EnhancedServiceLoader.class.getClassLoader();
    }

    private static class InnerEnhancedServiceLoader<T>{
        private static final Logger log = LoggerFactory.getLogger(InnerEnhancedServiceLoader.class);
        private static final String SERVICES_DIRECTORY = "META-INF/services/";
        private static final String MIEYDE_TX_DIRECTORY = "META-INF/mieyde/";
        private final Class<T> type;
        private final Holder<List<ExtensionDefinition<T>>> definitionsHolder = new Holder<>();
        private final ConcurrentMap<ExtensionDefinition<T>,Holder<Object>> definitionToInstanceMap = new ConcurrentHashMap<>();
        private final ConcurrentMap<String,List<ExtensionDefinition<T>>> nameToDefinitionsMap = new ConcurrentHashMap<>();
        private final ConcurrentMap<Class<?>,ExtensionDefinition<T>> classToDefinitionMap = new ConcurrentHashMap<>();

        public InnerEnhancedServiceLoader(Class<T> type) {
            this.type = type;
        }

        /**
         * 根据class获取并缓存InnerEnhancedServiceLoader
         */
        private static <T> InnerEnhancedServiceLoader<T> getServiceLoader(Class<T> type){
            if (ObjectUtils.isNull(type)){
                throw new IllegalArgumentException("Enhanced Service type is null");
            }
            return (InnerEnhancedServiceLoader<T>) CollectionUtils.computeIfAbsent(SERVICE_LOADERS,type, key -> new InnerEnhancedServiceLoader<>(type));
        }

        /**
         * 根据class移除nnerEnhancedServiceLoader
         */
        private static <T> InnerEnhancedServiceLoader<T> removeServiceLoader(Class<T> type){
            if (ObjectUtils.isNull(type)){
                throw new IllegalArgumentException("Enhanced Service type is null");
            }
            return (InnerEnhancedServiceLoader<T>) SERVICE_LOADERS.remove(type);
        }

        /**
         * 清空缓存
         */
        private static void removeAllServiceLoader(){
            SERVICE_LOADERS.clear();
        }

        private T load(ClassLoader loader){
            return loadExtension(loader,null,null);
        }

        private T load(String activateName,ClassLoader loader){
            return loadExtension(activateName,loader,null,null);
        }

        private T load(String activateName,Object[] args,ClassLoader loader){
            Class<?>[] argsType = null;
            if (CollectionUtils.isNotEmpty(args)){
                argsType = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    argsType[i] = args[i].getClass();
                }
            }
            return loadExtension(activateName,loader,argsType,args);
        }

        private T load(String activateName,Class<?>[] argsType,Object[] args,ClassLoader loader){
            return loadExtension(activateName,loader,argsType,args);
        }

        private List<T> loadAll(ClassLoader loader){
            return loadAll(null,null,loader);
        }

        private List<T> loadAll(Class<?>[] argsType,Object[] args,ClassLoader loader){
            List<T> allInstances = new ArrayList<>();
            List<Class<T>> allClazzs = getAllExtensionClass(loader);
            if (CollectionUtils.isEmpty(allClazzs)){
                return allInstances;
            }

            try {
                for (Class<T> clazz : allClazzs) {
                    ExtensionDefinition<T> definition = classToDefinitionMap.get(clazz);
                    allInstances.add(getExtensionInstance(definition, loader, argsType, args));
                }
            } catch (Throwable t) {
                throw new EnhancedServiceNotFoundException(t);
            }
            return allInstances;
        }

        private List<Class<T>> getAllExtensionClass(ClassLoader loader){
            return loadAllExtensionClass(loader);
        }

        private T loadExtension(ClassLoader loader,Class<?>[] argTypes,Object[] args){
            loadAllExtensionClass(loader);
            ExtensionDefinition<T> defaultExtensionDefinition = getDefaultExtensionDefinition();
            return getExtensionInstance(defaultExtensionDefinition,loader,argTypes,args);
        }

        private T loadExtension(String activateName, ClassLoader loader, Class[] argTypes,Object[] args) {
            if (StringUtils.isBlank(activateName)){
                throw new IllegalArgumentException("the name of service provider for [" + type.getName() + "] name is null");
            }
            loadAllExtensionClass(loader);
            ExtensionDefinition<T> cachedExtensionDefinition = getCachedExtensionDefinition(activateName);
            return getExtensionInstance(cachedExtensionDefinition, loader, argTypes, args);
        }

        private T getExtensionInstance(ExtensionDefinition<T> definition, ClassLoader loader, Class<?>[] argTypes,Object[] args) {
            if (ObjectUtils.isNull(definition)) {
                throw new EnhancedServiceNotFoundException("not found service provider for : " + type.getName());
            }
            if (ObjectUtils.equals(Scope.SINGLETON,definition.getScope())){
                //创建单例对象
                Holder<Object> holder = CollectionUtils.computeIfAbsent(definitionToInstanceMap, definition, key -> new Holder<>());
                Object instance = holder.get();
                if (ObjectUtils.isNull(instance)){
                    synchronized (holder){
                        instance = holder.get();
                        if (ObjectUtils.isNull(instance)){
                            instance = createNewExtension(definition, loader, argTypes, args);
                            holder.set(instance);
                        }
                    }
                }
                return (T) instance;
            }
            return createNewExtension(definition,loader,argTypes,args);
        }

        private T createNewExtension(ExtensionDefinition<T> definition, ClassLoader loader, Class<?>[] argTypes, Object[] args) {
            Class<T> clazz = definition.getServiceClass();
            try {
                return initInstance(clazz, argTypes, args);
            } catch (Throwable t) {
                throw new IllegalStateException("Extension instance(definition: " + definition + ", class: " + type + ")  could not be instantiated: " + t.getMessage(), t);
            }
        }

        private List<Class<T>> loadAllExtensionClass(ClassLoader loader){
            List<ExtensionDefinition<T>> definitions = definitionsHolder.get();
            if (CollectionUtils.isEmpty(definitions)){
                synchronized (definitionsHolder){
                    definitions = definitionsHolder.get();
                    if (CollectionUtils.isEmpty(definitions)){
                        definitions = findAllExtensionDefinition(loader);
                        definitionsHolder.set(definitions);
                    }
                }
            }
            return definitions.stream().map(ExtensionDefinition::getServiceClass).collect(Collectors.toList());
        }

        private List<ExtensionDefinition<T>> findAllExtensionDefinition(ClassLoader loader) {
            List<ExtensionDefinition<T>> extensionDefinitions = new ArrayList<>();
            try {
                //加载文件路径
                loadFile(SERVICES_DIRECTORY, loader, extensionDefinitions);
                loadFile(MIEYDE_TX_DIRECTORY, loader, extensionDefinitions);
            } catch (IOException e) {
                throw new EnhancedServiceNotFoundException(e);
            }

            if (CollectionUtils.isNotEmpty(nameToDefinitionsMap)){
                //已有的数据排序处理
                for (List<ExtensionDefinition<T>> definitions : nameToDefinitionsMap.values()) {
                    definitions.sort((def1,def2) -> Integer.compare(def1.getOrder(),def2.getOrder()));
                }
            }

            if (CollectionUtils.isNotEmpty(extensionDefinitions)){
                //加载的数据进行排序
                extensionDefinitions.sort((def1,def2) -> Integer.compare(def1.getOrder(),def2.getOrder()));
            }
            return extensionDefinitions;
        }

        private void loadFile(String dir, ClassLoader loader, List<ExtensionDefinition<T>> extensions) throws IOException {
            String fileName = dir + type.getName();
            Enumeration<URL> urls = null;
            if (loader != null){
                urls = loader.getResources(fileName);
            }else {
                urls = ClassLoader.getSystemResources(fileName);
            }
            if (urls != null){
                boolean hasServiceFile = false;
                boolean hasClasses = false;
                while (urls.hasMoreElements()){
                    hasServiceFile = true;
                    URL url = urls.nextElement();
                    try(BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), Constants.DEFAULT_CHARSET))){
                        String line;
                        while ((line = reader.readLine()) != null){
                            final int ci = line.indexOf('#');
                            if (ci > 0){
                                line = line.substring(0,ci);
                            }
                            line = line.trim();
                            if (StringUtils.isNotBlank(line)){
                                hasClasses = true;
                                try {
                                    ExtensionDefinition<T> extensionDefinition = getUnloadedExtensionDefinition(line,loader);
                                    if (ObjectUtils.isNull(extensionDefinition)){
                                        if (log.isDebugEnabled()){
                                            log.debug("The same extension {} has already been loaded, skipped", line);
                                        }
                                        continue;
                                    }
                                    extensions.add(extensionDefinition);
                                } catch (ClassNotFoundException e) {
                                    log.warn("Load [{}] class fail: {}", line, e.getMessage());
                                }
                            }
                        }
                    }
                }

                if (log.isDebugEnabled()) {
                    if (!hasServiceFile) {
                        log.warn("Load [{}] class fail: no service files found in '{}'.", type.getName(), dir);
                    } else if (!hasClasses) {
                        log.warn("Load [{}] class fail: the service files in '{}' is all empty.", type.getName(), dir);
                    }
                }
            }

            if (log.isDebugEnabled()) {
                log.warn("Load [{}] class fail: no urls found in '{}'.", type.getName(), dir);
            }
        }

        private ExtensionDefinition<T> getUnloadedExtensionDefinition(String className, ClassLoader loader) throws ClassNotFoundException {
            if (!isDefinitionContainsClazz(className,loader)){
                Class<?> clazz = Class.forName(className, true, loader);
                if (!type.isAssignableFrom(clazz)){
                    log.error("can't cast {} to {}", clazz.getName(), type.getName());
                    throw new ClassCastException();
                }
                Class<T> enhancedServiceClass = (Class<T>) clazz;
                String serviceName = null;
                int priority = 0;
                Scope scope = Scope.SINGLETON;
                LoadLevel loadLevel = clazz.getAnnotation(LoadLevel.class);
                if (loadLevel != null){
                    serviceName = loadLevel.name();
                    priority = loadLevel.order();
                    scope = loadLevel.scope();
                }

                ExtensionDefinition<T> result = new ExtensionDefinition<>(serviceName, enhancedServiceClass, priority, scope);
                classToDefinitionMap.put(clazz,result);
                if (StringUtils.isNotBlank(serviceName)){
                    CollectionUtils.computeIfAbsent(nameToDefinitionsMap,serviceName, key -> new ArrayList<>()).add(result);
                }
                return result;
            }
            return null;
        }

        private boolean isDefinitionContainsClazz(String className, ClassLoader loader) {
            for (Map.Entry<Class<?>, ExtensionDefinition<T>> entry : classToDefinitionMap.entrySet()) {
                if (!StringUtils.equals(entry.getKey().getName(),className)){
                    continue;
                }
                if (ObjectUtils.equals(entry.getValue().getServiceClass().getClassLoader(),loader)){
                    return true;
                }
            }
            return false;
        }

        private ExtensionDefinition<T> getDefaultExtensionDefinition() {
            List<ExtensionDefinition<T>> currentDefinitions = definitionsHolder.get();
            return CollectionUtils.getLast(currentDefinitions);
        }

        private ExtensionDefinition<T> getCachedExtensionDefinition(String activateName) {
            List<ExtensionDefinition<T>> definitions = nameToDefinitionsMap.get(activateName);
            return CollectionUtils.getLast(definitions);
        }

        /**
         * 实例化对象
         */
        private T initInstance(Class<T> implClazz,Class<?>[] argTypes,Object[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            T t = null;
            if (CollectionUtils.isNotEmpty(argTypes) && CollectionUtils.isNotEmpty(args)){
                Constructor<T> constructor = implClazz.getDeclaredConstructor(argTypes);
                t = type.cast(constructor.newInstance(args));
            }else {
                t = type.cast(implClazz.newInstance());
            }
            if (t instanceof Initialize){
                //如果此类基础初始化方法接口，实例化对象时执行此类的初始化方法
                ((Initialize) t).init();
            }
            return t;
        }

        private static class Holder<S> {
            private volatile S value;

            public void set(S value) {
                this.value = value;
            }

            public S get() {
                return value;
            }
        }
    }
}
