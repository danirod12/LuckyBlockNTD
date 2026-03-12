package me.DenBeKKer.ntdLuckyBlock.api.exception;

public class DependencyNotFoundException extends RuntimeException {

    public DependencyNotFoundException(String dependency) {
        super("Dependency " + dependency + " was not found, but requested");
    }
}
