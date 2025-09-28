package de.jotschi.ai.deepthought.memory.impl;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import de.jotschi.ai.deepthought.memory.KnowledgeGraph;

public class Neo4jKnowledgeGraph implements KnowledgeGraph {

    public void test() {
        // URI examples: "neo4j://localhost", "neo4j+s://xxx.databases.neo4j.io"
        final String dbUri = "<database-uri>";
        final String dbUser = "<username>";
        final String dbPassword = "<password>";

        try (Driver driver = GraphDatabase.driver(dbUri, AuthTokens.basic(dbUser, dbPassword))) {
            driver.verifyConnectivity();
            System.out.println("Connection established.");
        }
    }
}
