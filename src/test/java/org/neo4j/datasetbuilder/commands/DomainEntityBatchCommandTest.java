package org.neo4j.datasetbuilder.commands;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.neo4j.datasetbuilder.DomainEntity.domainEntity;
import static org.neo4j.datasetbuilder.commands.DomainEntityBatchCommandBuilder.createEntities;

import org.junit.Test;
import org.neo4j.datasetbuilder.BatchCommandExecutor;
import org.neo4j.datasetbuilder.DomainEntity;
import org.neo4j.datasetbuilder.DomainEntityInfo;
import org.neo4j.datasetbuilder.Run;
import org.neo4j.datasetbuilder.logging.SysOutLog;
import org.neo4j.datasetbuilder.test.Db;
import org.neo4j.graphdb.GraphDatabaseService;

public class DomainEntityBatchCommandTest
{
    @Test
    public void shouldCreateEntityPrefixedWithEntityName() throws Exception
    {
        // given
        GraphDatabaseService db = Db.impermanentDb();
        BatchCommandExecutor executor = new BatchCommandExecutor( db, SysOutLog.INSTANCE );
        Run run = executor.newRun( "Test" );
        DomainEntity user = domainEntity( "user", true );

        // when
        createEntities( user ).quantity( 1 ).execute( run );

        // then
        assertEquals( "user-1", db.getNodeById( 1 ).getProperty( "name" ) );
    }

    @Test
    public void shouldIndexIndexableEntity() throws Exception
    {
        // given
        GraphDatabaseService db = Db.impermanentDb();
        BatchCommandExecutor executor = new BatchCommandExecutor( db, SysOutLog.INSTANCE );
        Run run = executor.newRun( "Test" );
        DomainEntity user = domainEntity( "user", "name", true );

        // when
        createEntities( user ).quantity( 1 ).execute( run );

        // then
        assertNotNull( db.index().forNodes( "user" ).get( "name", "user-1" ).getSingle() );
    }

    @Test
    public void shouldSupplyUserDefinedPropertyName() throws Exception
    {
        // given
        GraphDatabaseService db = Db.impermanentDb();
        BatchCommandExecutor executor = new BatchCommandExecutor( db, SysOutLog.INSTANCE );
        Run run = executor.newRun( "Test" );
        DomainEntity user = domainEntity( "user", "key", true );

        // when
        createEntities( user ).quantity( 1 ).execute( run );

        // then
        assertEquals( "user-1", db.getNodeById( 1 ).getProperty( "key" ) );
    }

    @Test
    public void shouldReturnListOfCreatedNodeIds() throws Exception
    {
        // given
        GraphDatabaseService db = Db.impermanentDb();
        BatchCommandExecutor executor = new BatchCommandExecutor( db, SysOutLog.INSTANCE );
        Run run = executor.newRun( "Test" );
        DomainEntity user = domainEntity( "user", "key", true );

        // when
        DomainEntityInfo results = createEntities( user ).quantity( 5 ).execute( run );

        // then
        assertEquals( asList( 1l, 2l, 3l, 4l, 5l ), results.nodeIds() );
    }
}
