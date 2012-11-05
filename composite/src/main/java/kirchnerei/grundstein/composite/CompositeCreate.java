package kirchnerei.grundstein.composite;

public interface CompositeCreate {

	/**
	 * Call the create method directly after create the instance and before the init method of
	 * CompositeInit interface.
	 *
	 * @param builder the composite builder
	 */
	void create(CompositeBuilder builder);
}
