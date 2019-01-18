package picoded.core.struct.template;

import java.util.Collection;

import picoded.core.common.SystemSetupInterface;

/**
 * Convinence interface, used to provide systemSetupInterface collection support to its inner calls
 * Making the collection compliant with the SystemSetupInterface
 */
public interface AbstractSystemSetupInterfaceCollection extends SystemSetupInterface {
	
	/**
	 * [TO OVERWRITE] : SystemSetupInterface collection used by subsequent  
	 * subcalls via AbstractSystemSetupInterfaceCollection
	 **/
	Collection<SystemSetupInterface> systemSetupInterfaceCollection();
	
	//--------------------------------------------------------------------------
	//
	// Backend system setup / teardown / maintenance
	//
	//--------------------------------------------------------------------------
	
	/**
	 * Does onetime set up of the backend storage. If needed.
	 * The SQL equivalent would be "CREATE TABLE {TABLENAME} IF NOT EXISTS"
	 **/
	default void systemSetup() {
		systemSetupInterfaceCollection().forEach(item -> item.systemSetup());
	}
	
	/**
	 * Destroy, Teardown and delete the backend storage. If needed
	 * The SQL equivalent would be "DROP TABLE {TABLENAME}"
	 **/
	default void systemDestroy() {
		systemSetupInterfaceCollection().forEach(item -> item.systemDestroy());
	}
	
	/**
	 * Perform maintenance, this is meant for large maintenance jobs.
	 * Such as weekly or monthly compaction. It may or may not be a long
	 * running task, where its use case is backend specific.
	 *
	 * A common example is stop the world cleanup for a database backend
	 **/
	default void maintenance() {
		systemSetupInterfaceCollection().forEach(item -> item.maintenance());
	}
	
	/**
	 * Perform increment maintenance, meant for minor changes between requests. If called.
	 *
	 * By default this randomly triggers a maintenance call with 2% probability.
	 * The main reason for doing so, is that for many implmentations there may not be
	 * a concept of incremental maintenance, and in many cases its implementor may forget
	 * to actually call a maintenance call. For years.
	 *
	 * Unless the maintenance call is too expensive, (eg more then 2 seconds), having
	 * it randomly trigger and slow down one transaction randomly. Helps ensure everyone,
	 * systems is more performant in overall.
	 *
	 * It is a very controversal decision, however as awsome as your programming or
	 * devops team is. Your client and their actual infrastructure may be "not as awesome"
	 *
	 * In other cases there might be small incremental maintenance that can be done,
	 * speicifc to a module, in which this call works as intended.
	 **/
	default void incrementalMaintenance() {
		systemSetupInterfaceCollection().forEach(item -> item.incrementalMaintenance());
	}
	
	//--------------------------------------------------------------------------
	//
	// Clearing of data
	//
	//--------------------------------------------------------------------------
	
	/**
	 * Removes all data, without tearing down setup
	 *
	 * This is equivalent of "TRUNCATE TABLE {TABLENAME}"
	 **/
	default void clear() {
		systemSetupInterfaceCollection().forEach(item -> item.clear());
	}
	
	//--------------------------------------------------------------------------
	//
	// Does the closure of any underlying connection if needed
	//
	//--------------------------------------------------------------------------
	
	/**
	 * Perform any required connection / file handlers / etc closure
	 * This is to clean up any "resource" usage if needed.
	 * 
	 * Particularly important for JSQL backend implementation (for example)
	 * 
	 * Note that unlike the "AutoCloseable" specification. This is REQUIRED
	 * to be indepotent, as it will be called multiple times
	 * 
	 * Also as per our "standard" its exception type is limited to RuntimeException
	 * This reduces the "warning" for possible interrupt exception types in java.
	 */
	default void close() {
		systemSetupInterfaceCollection().forEach(item -> item.close());
	}
}