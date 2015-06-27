package utils.async_stuff;

/**
 * The base interface that an operations ("Ops") class must implement
 * so that it can be notified automatically by the GenericActivity
 * framework when runtime configuration changes occur.
 */
public interface ConfigurableOps {
    /**
     * Hook method dispatched by the GenericOps framework to
     * initialize an operations ("Ops") object after it's been created.
     *
     * @param firstTimeIn  Set to "true" if this is the first time the
     *                     Ops class is initialized, else set to
     *                     "false" if called after a runtime
     *                     configuration change.
     */
    void onConfiguration(boolean firstTimeIn);
}
