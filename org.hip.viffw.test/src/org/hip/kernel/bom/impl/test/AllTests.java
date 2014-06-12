package org.hip.kernel.bom.impl.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AbstractStatisticsHomeImplTest.class,
		AlternativeModellTest.class, BetweenObjectImplTest.class,
		BlobHomeImplTest.class, CriteriumValueStrategyTest.class,
		DefaultDBAdapterSimpleTest.class, DefaultNameValueTest.class,
		DefaultSemanticObjectTest.class, DomainObjectCacheImplTest.class,
		DomainObjectCollectionImplTest.class, DomainObjectHomeImplTest.class,
		DomainObjectImplTest.class, EmptyQueryResultTest.class,
		ExtDBQueryStatementTest.class, HavingObjectImplTest.class,
		HomeManagerImplTest.class, InObjectImplTest.class,
		InsertStatementTest.class, JoinedDomainObjectHomeImplTest.class,
		KeyCriterionImplTest.class, KeyObjectImplTest.class,
		LDAPCriteriaStackTest.class, ModifierStrategyTest.class,
		MySQLAdapterJoinTest.class, NestedJoinTest.class,
		OrderItemImplTest.class, OrderObjectImplTest.class, PageImplTest.class,
		PlaceholderHomeTest.class, PreparedStatementTest.class,
		PropertyImplTest.class, PropertySetImplTest.class,
		QueryResultTest.class, QueryStatementTest.class, SecurityTest.class,
		SerializerTest.class, SetOperatorHomeImplTest.class,
		SingleValueQueryStatementImplTest.class, SortedArrayTest.class,
		SQLCriteriaStackTest.class, UpdateStatementTest.class,
		ValueForSQLTest.class, XMLCharacterFilterTest.class })
public class AllTests {

}
