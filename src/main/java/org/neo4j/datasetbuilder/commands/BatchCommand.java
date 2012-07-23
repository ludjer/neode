/*
 * Copyright (C) 2012 Neo Technology
 * All rights reserved
 */
package org.neo4j.datasetbuilder.commands;

import java.util.Random;

import org.neo4j.datasetbuilder.DomainEntityInfo;
import org.neo4j.datasetbuilder.logging.Log;
import org.neo4j.graphdb.GraphDatabaseService;

public interface BatchCommand
{
    int numberOfIterations();
    int batchSize();
    void execute( GraphDatabaseService db, int index, Random random );
    String description();
    String shortDescription();
    void onBegin(Log log);
    void onEnd(Log log);
    DomainEntityInfo results();
}