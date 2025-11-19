package org.xu.project;

import org.xu.exception.XuException;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("ALL")
public class XuProjectFileReader {

    private XuProject project;

    public XuProjectFileReader(String projectFilepath) {
        String file = getFileContent(projectFilepath);
        LoaderOptions options = new LoaderOptions();
        Yaml yaml = new Yaml(options);
        Map map = (Map) yaml.load(file);

        if (map.containsKey("project")) {
            project = loadProject((Map) map.get("project"));
        } else {
            throw new XuException("Could not load project");
        }
    }

    private XuProject loadProject(Map map) {
        XuProjectDescriptor descriptor = loadProjectDescriptor(map);
        String mainFile = map.get("main").toString();
        Boolean isScript = map.get("script").toString().equals("true");
        XuKernelConfiguration kernelConfiguration = loadKernelConfiguration(map);
        XuDependencies dependencies = loadDependencies(map);
        XuEnvironmentDescriptor environmentDescriptor = loadEnvironmentDescriptor(map);
        XuInterceptorConfiguration interceptorConfiguration = loadInterceptorConfiguration(map);
        XuExternalInterceptorConfiguration externalInterceptorConfiguration = loadExternalInterceptorConfiguration(map);
        return new XuProject(descriptor, mainFile, isScript, kernelConfiguration, dependencies, environmentDescriptor,
                interceptorConfiguration, externalInterceptorConfiguration);
    }

    private XuExternalInterceptorConfiguration loadExternalInterceptorConfiguration(Map map) {
        boolean enableExternalInterceptors = false;
        List<String> names = new ArrayList<>();

        if (map.containsKey("environment")) {
            Map environmentMap = (Map) map.get("environment");
            if (environmentMap.containsKey("enable-external-interceptors")) {
                enableExternalInterceptors = Boolean.parseBoolean(environmentMap.get("enable-external-interceptors").toString());
            }

            if (environmentMap.containsKey("external-interceptors")) {
                List<String> list = (List<String>) environmentMap.get("external-interceptors");
                names.addAll(list);
            }
        } else {
            throw new XuException("Could not load interceptors.");
        }

        return new

                XuExternalInterceptorConfiguration(enableExternalInterceptors, names);
    }

    private XuInterceptorConfiguration loadInterceptorConfiguration(Map map) {
        List<XuInterceptorDescriptor> interceptorDescriptors = new ArrayList<>();

        if (map.containsKey("environment")) {
            Map environmentMap = (Map) map.get("environment");

            if (environmentMap.containsKey("internal-interceptors")) {
                Map interceptorMap = (Map) environmentMap.get("internal-interceptors");

                for (Object key : interceptorMap.keySet()) {
                    interceptorDescriptors.add(new XuInterceptorDescriptor(key.toString(), interceptorMap.get(key).toString().equals("true")));
                }
            } else {
                // TODO
            }
        } else {
            throw new XuException("Could not load interceptors.");
        }

        return new XuInterceptorConfiguration(interceptorDescriptors);
    }

    private XuEnvironmentDescriptor loadEnvironmentDescriptor(Map map) {
        if (map.containsKey("environment")) {
            Map environmentMap = (Map) map.get("environment");
            return new XuEnvironmentDescriptor(environmentMap.get("debug").toString().equals("true"),
                    XuOptimizationLevel.valueOf(environmentMap.get("optimization-level").toString().toUpperCase()));
        } else {
            throw new XuException("No environment found.");
        }
    }

    private XuDependencies loadDependencies(Map map) {

        List<XuDependency> dependencies = new ArrayList<>();
        if (map.containsKey("dependencies")) {
            List<Map> dependenciesList = (List<Map>) map.get("dependencies");

            for (Map dependency : (List<Map>) dependenciesList) {
                dependencies.add(loadDependency(dependency));
            }
        }

        return new XuDependencies(dependencies);
    }

    private XuDependency loadDependency(Map dependency) {
        return new XuDependency(dependency.get("name").toString(), dependency.get("version").toString());
    }

    private XuKernelConfiguration loadKernelConfiguration(Map<String, Object> map) {
        if (map.containsKey("kernels")) {
            Map description = (Map) map.get("kernels");

            return new XuKernelConfiguration(description.get("enabled").toString().equals("true"),
                    XuGPUTarget.valueOf(description.get("gpu-target").toString().toUpperCase()),
                    parseMemoryLimits(description.get("memory-limits").toString()));
        } else {
            throw new XuException("Missing project description.");
        }
    }

    private Float parseMemoryLimits(String limits) {
        if (limits.contains("GB")) {
            String rawValue = limits.replaceAll("GB", "");
            return Float.parseFloat(rawValue) * 1024f * 1024f * 1024f;
        } else if (limits.contains("MB")) {
            String rawValue = limits.replaceAll("MB", "");
            return Float.parseFloat(rawValue) * 1024f * 1024f;
        } else if (limits.contains("KB")) {
            String rawValue = limits.replaceAll("KB", "");
            return Float.parseFloat(rawValue) * 1024f;
        } else {
            return Float.parseFloat(limits);
        }
    }

    private XuProjectDescriptor loadProjectDescriptor(Map map) {
        if (map.containsKey("description")) {
            Map description = (Map) map.get("description");
            return new XuProjectDescriptor((String) description.get("name"),
                    (String) description.get("version"),
                    (Boolean) description.get("alpha"));
        } else {
            throw new XuException("Missing project description.");
        }
    }

    private String getFileContent(String filepath) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

        } catch (IOException e) {
            throw new XuException(e.getMessage());
        }

        return sb.toString();
    }

    public XuProject getProject() {
        return project;
    }
}
