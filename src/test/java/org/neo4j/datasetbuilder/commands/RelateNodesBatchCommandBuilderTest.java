package org.neo4j.datasetbuilder.commands;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.neo4j.datasetbuilder.DomainEntity.domainEntity;
import static org.neo4j.datasetbuilder.commands.DomainEntityBatchCommandBuilder.createEntities;
import static org.neo4j.datasetbuilder.commands.RelateNodesBatchCommandBuilder.relateEntities;
import static org.neo4j.graphdb.DynamicRelationshipType.withName;

import java.util.Random;

import org.junit.Test;
import org.neo4j.datasetbuilder.BatchCommandExecutor;
import org.neo4j.datasetbuilder.DomainEntity;
import org.neo4j.datasetbuilder.DomainEntityInfo;
import org.neo4j.datasetbuilder.Run;
import org.neo4j.datasetbuilder.finders.NodeFinderStrategy;
import org.neo4j.datasetbuilder.logging.SysOutLog;
import org.neo4j.datasetbuilder.test.Db;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

public class RelateNodesBatchCommandBuilderTest
{
    @Test
    public void shouldRelateNodes() throws Exception
    {
        // given
        GraphDatabaseService db = Db.impermanentDb();
        BatchCommandExecutor executor = new BatchCommandExecutor( db, SysOutLog.INSTANCE );
        Run run = executor.newRun( "Test" );
        DomainEntityInfo users = createEntities( domainEntity( "user" ) ).quantity( 3 ).execute( run );
        DomainEntity product = domainEntity( "product" );
        final DomainEntityInfo products = createEntities( product ).quantity( 3 ).execute( run );
        NodeFinderStrategy finderStrategy = new NodeFinderStrategy()
        {
            int index = 0;

            @Override
            public Iterable<Node> getNodes( GraphDatabaseService db, Node currentNode, int numberOfNodes, Random random )
            {
                return asList( db.getNodeById( products.nodeIds().get( index++ ) ) );
            }

            @Override
            public String entityName()
            {
                return null;
            }
        };

        // when
        relateEntities( users ).to( finderStrategy ).relationship( withName("BOUGHT") )
                .cardinality( Range.exactly( 1 ) ).execute( run );

        // then
        DynamicRelationshipType bought = withName( "BOUGHT" );

        Node product1 = db.getNodeById( 1 );
        assertTrue( product1.hasRelationship( bought, Direction.OUTGOING ) );
        assertEquals( 4l, product1.getSingleRelationship( bought, Direction.OUTGOING ).getEndNode().getId() );

        Node product2 = db.getNodeById( 2 );
        assertTrue( product2.hasRelationship( bought, Direction.OUTGOING ) );
        assertEquals( 5l, product2.getSingleRelationship( bought, Direction.OUTGOING ).getEndNode().getId() );

        Node product3 = db.getNodeById( 3 );
        assertTrue( product3.hasRelationship( bought, Direction.OUTGOING ) );
        assertEquals( 6l, product3.getSingleRelationship( bought, Direction.OUTGOING ).getEndNode().getId() );
    }
}
