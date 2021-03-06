/*
 * Copyright (C) 2012 Neo Technology
 * All rights reserved
 */
package org.neo4j.neode;

import java.util.HashSet;

import org.neo4j.graphdb.GraphDatabaseService;

enum NodeCollectionFactory
{
    INSTANCE
            {
                @Override
                NodeCollection createNodeCollection( GraphDatabaseService db, String label )
                {
                    return new NodeCollection( db, label, new HashSet<Long>() );
                }
            },
    NULL
            {
                @Override
                NodeCollection createNodeCollection( GraphDatabaseService db, String label )
                {
                    return NodeCollection.NULL;
                }
            };

    abstract NodeCollection createNodeCollection( GraphDatabaseService db, String label );
}
