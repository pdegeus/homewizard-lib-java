package nl.rgonline.homewizardlib.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.apache.commons.lang.BooleanUtils;

/**
 * Configuration item. 
 * @param <T> Type of the value.
 * @author pdegeus
 */
public class HWConfigItem<T> {

    private final String propertyName;
    
    private final boolean hasDefault;
    private final T defaultValue;
    
    private final Class<T> type;

    /**
     * Constructor without default value. Does not allow reading from shared config.
     * @param propertyName JNDI property name to look for.
     * @param typeClass The class reference for type T. Used to verify values.
     */
    public HWConfigItem(String propertyName, Class<T> typeClass) {
        this(propertyName, typeClass, null);
    }
    
    /**
     * Constructor with all options. 
     * @param propertyName JNDI property name to look for.
     * @param typeClass The class reference for type T. Used to verify values.
     * @param defaultValue Default value to return when no value is found.
     */
    public HWConfigItem(String propertyName, Class<T> typeClass, T defaultValue) {
        this.propertyName = propertyName;
        this.type = typeClass;
        this.defaultValue = defaultValue;
        this.hasDefault = (defaultValue != null);
    }
    
    /**
     * @return The type of the value of this configuration property as determined from the
     * generic type {@code <T>}.
     */
    public Class<T> getType() {
        return type;
    }
    
    /**
     * @return JNDI property name this config property looks for.
     */
    public String getPropertyName() {
        return propertyName;
    }
    
    /**
     * @return The default value for this config property, or null.
     */
    public T getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * @return True if this config property has a default value to fallback on.
     */
    public boolean hasDefault() {
        return hasDefault;
    }
    
    /**
     * @return True if for this config property a value is set by the user.
     */
    public boolean hasValue() {
        return getRawValue() != null;
    }

    @Override
    public String toString() {
        return "[ConfigProperty " + getPropertyName() + ']';
    }
    
    /**
     * Returns the configuration value found in JNDI (either project or shared) or the default value, depending on the way
     * this instance has been constructed.
     * @return Config property value, or null.
     */
    public T getValue() {
        String value = getRawValue();

        // No value found, use default or throw exception
        if (value == null) {
            if (hasDefault()) {
                return getDefaultValue();
            } else {
                throw new MissingConfigurationException(this);
            }
        }

        return checkAndConvertValue(value);
    }

    private String getRawValue() {
        Properties settings = HWConfig.getProjectSettings();
        if (settings == null) {
            return null;
        }

        String propName = getPropertyName();
        return settings.getProperty(propName);
    }

    @SuppressWarnings("unchecked")
    private T checkAndConvertValue(String value) {
        
        //Check boolean for valid value
        if (type.equals(Boolean.class)) {
            return (T) BooleanUtils.toBooleanObject(value);
            
        //Strings can be returned directly
        } else if (type.equals(String.class)) {
            return (T) value;
        }
        
        //Construct new instance of type with constructor with String param
        try {
            Constructor<T> construct = type.getConstructor(String.class);
            return construct.newInstance(value);
        } catch (InvocationTargetException | IllegalArgumentException | InstantiationException e) {
            throw new InvalidPropertyTypeException(this, e);
        } catch (Exception e) {
            String msg = String.format("Could not convert config property value for '%s', type %s", getPropertyName(), type.getClass().getName());
            throw new InvalidPropertyTypeException(msg, e);
        }
    }
    
}
