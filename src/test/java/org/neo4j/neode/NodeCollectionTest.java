package org.neo4j.neode;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Iterator;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.neode.test.Db;

public class NodeCollectionTest
{
    @Test
    public void shouldReturnNodeByPosition() throws Exception
    {
        Db.usingSampleDataset( new Db.WithSampleDataset()
        {
            @Override
            public void execute( GraphDatabaseService db, Node firstNode, Node secondNode, Node thirdNode )
            {
                // given
                NodeCollection nodeCollection = new NodeCollection( db, "user",
                        asList( firstNode.getId(), secondNode.getId(), thirdNode.getId() ) );

                // then
                assertEquals( firstNode, nodeCollection.getNodeByPosition( 0 ) );
                assertEquals( secondNode, nodeCollection.getNodeByPosition( 1 ) );
                assertEquals( thirdNode, nodeCollection.getNodeByPosition( 2 ) );
            }
        } );
    }

    @Test
    public void shouldBeAbleToIterateNodes() throws Exception
    {
        Db.usingSampleDataset( new Db.WithSampleDataset()
        {
            @Override
            public void execute( GraphDatabaseService db, Node firstNode, Node secondNode, Node thirdNode )
            {
                // given
                NodeCollection nodeCollection = new NodeCollection( db, "user",
                        asList( firstNode.getId(), secondNode.getId(), thirdNode.getId() ) );

                // then
                Iterable<Node> expectedNodes = asList( firstNode, secondNode, thirdNode );
                assertThat( nodeCollection, returnsSameItems( expectedNodes ) );
            }
        } );
    }

    @Test
    public void shouldBeAbleToAddNode() throws Exception
    {
        Db.usingSampleDataset( new Db.WithSampleDataset()
        {
            @Override
            public void execute( GraphDatabaseService db, Node firstNode, Node secondNode, Node thirdNode )
            {
                // given
                NodeCollection nodeCollection = new NodeCollection( db, "user", new ArrayList<Long>() );

                // when
                nodeCollection.add( firstNode );
                nodeCollection.add( secondNode );
                nodeCollection.add( thirdNode );

                // then
                Iterable<Node> expectedNodes = asList( firstNode, secondNode, thirdNode );
                assertThat( nodeCollection, returnsSameItems( expectedNodes ) );
            }
        } );
    }

    @Test
    public void shouldReturnLabel() throws Exception
    {
        Db.usingSampleDataset( new Db.WithSampleDataset()
        {
            @Override
            public void execute( GraphDatabaseService db, Node firstNode, Node secondNode, Node thirdNode )
            {
                // given
                NodeCollection nodeCollection = new NodeCollection( db, "user",
                        asList( firstNode.getId(), secondNode.getId(), thirdNode.getId() ) );
                // then
                assertEquals( "user", nodeCollection.label() );
            }
        } );
    }

    @Test
    public void shouldReturnNumberOfNodes() throws Exception
    {

        Db.usingSampleDataset( new Db.WithSampleDataset()
        {
            @Override
            public void execute( GraphDatabaseService db, Node firstNode, Node secondNode, Node thirdNode )
            {
                // given
                NodeCollection nodeCollection = new NodeCollection( db, "user",
                        asList( firstNode.getId(), secondNode.getId(), thirdNode.getId() ) );
                // then
                assertEquals( 3, nodeCollection.size() );
            }
        } );
    }

    @Test
    public void shouldReturnSubsetOfSelf() throws Exception
    {

        Db.usingSampleDataset( new Db.WithSampleDataset()
        {
            @Override
            public void execute( GraphDatabaseService db, Node firstNode, Node secondNode, Node thirdNode )
            {
                // given
                NodeCollection nodeCollection = new NodeCollection( db, "user",
                        asList( firstNode.getId(), secondNode.getId(), thirdNode.getId() ) );

                // when
                NodeCollection subset = nodeCollection.subset( asList( 0, 2 ) );

                // then
                Iterable<Node> expectedNodes = asList( firstNode, thirdNode );
                assertThat( subset, returnsSameItems( expectedNodes ) );
            }
        } );
    }

    @Test
    public void shouldBeAbleToCombineCollections() throws Exception
    {

        Db.usingSampleDataset( new Db.WithSampleDataset()
        {
            @Override
            public void execute( GraphDatabaseService db, Node firstNode, Node secondNode, Node thirdNode )
            {
                // given
                NodeCollection firstCollection = new NodeCollection( db, "user",
                        asList( firstNode.getId(), secondNode.getId() ) );
                NodeCollection secondCollection = new NodeCollection( db, "user",
                        asList( thirdNode.getId() ) );

                // when
                NodeCollection combined = firstCollection.combine( secondCollection );

                // then
                Iterable<Node> expectedNodes = asList( firstNode, secondNode, thirdNode );
                assertThat( combined, returnsSameItems( expectedNodes ) );
            }
        } );
    }

    @Test
    public void shouldNotBeAbleToAddSameNodeTwice() throws Exception
    {
        Db.usingSampleDataset( new Db.WithSampleDataset()
        {
            @Override
            public void execute( GraphDatabaseService db, Node firstNode, Node secondNode, Node thirdNode )
            {
                // given
                NodeCollection nodeCollection = new NodeCollection( db, "user", asList( firstNode.getId()) );

                // when
                nodeCollection.add( firstNode );

                // then
                Iterable<Node> expectedNodes = asList( firstNode );
                assertThat( nodeCollection, returnsSameItems( expectedNodes ) );
            }
        } );
    }

    @Test
    public void shouldIgnoreDuplicatesWhenCombiningCollections() throws Exception
    {
        Db.usingSampleDataset( new Db.WithSampleDataset()
        {
            @Override
            public void execute( GraphDatabaseService db, Node firstNode, Node secondNode, Node thirdNode )
            {
                // given
                NodeCollection firstCollection = new NodeCollection( db, "user",
                        asList( firstNode.getId(), secondNode.getId() ) );
                NodeCollection secondCollection = new NodeCollection( db, "user",
                        asList( secondNode.getId(), thirdNode.getId() ) );

                // when
                NodeCollection combined = firstCollection.combine( secondCollection );

                // then
                Iterable<Node> expectedNodes = asList( firstNode, secondNode, thirdNode );
                assertThat( combined, returnsSameItems( expectedNodes ) );
            }
        } );
    }

    private <T> Matcher<Iterable<T>> returnsSameItems( final Iterable<T> expected )
    {
        return new TypeSafeMatcher<Iterable<T>>()
        {
            @Override
            public boolean matchesSafely( Iterable<T> actual )
            {
                Iterator<T> expectedIterator = expected.iterator();
                Iterator<T> iterator = actual.iterator();
                boolean result = true;
                while ( expectedIterator.hasNext() && iterator.hasNext() )
                {

                    T expectedNext = expectedIterator.next();
                    T next = iterator.next();
                    if ( expectedNext == null && next == null )
                    {
                        result &= true;
                    }
                    else
                    {
                        result &= expectedNext.equals( next );
                    }
                }
                return result &= (!expectedIterator.hasNext()) && (!iterator.hasNext());
            }

            @Override
            public void describeTo( Description description )
            {
                description.appendText( "Iterables do not return the same items" );
            }
        };
    }

}
