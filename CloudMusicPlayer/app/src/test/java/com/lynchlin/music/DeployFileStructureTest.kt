package com.lynchlin.music

import org.junit.Assert.*
import org.junit.Test
import java.io.File

class DeployFileStructureTest {

    @Test
    fun `Dockerfile exists in deploy directory`() {
        val dockerfile = File("deploy/Dockerfile")
        assertTrue("Dockerfile should exist", dockerfile.exists())
        assertTrue("Dockerfile should not be empty", dockerfile.readText().isNotBlank())
    }

    @Test
    fun `docker-compose yml exists`() {
        val compose = File("deploy/docker-compose.yml")
        assertTrue("docker-compose.yml should exist", compose.exists())
        val content = compose.readText()
        assertTrue(content.contains("netease-api"))
        assertTrue(content.contains("build: ."))
    }

    @Test
    fun `nginx conf exists`() {
        val nginx = File("deploy/nginx.conf")
        assertTrue("nginx.conf should exist", nginx.exists())
        val content = nginx.readText()
        assertTrue(content.contains("proxy_pass"))
        assertTrue(content.contains("listen 80"))
    }

    @Test
    fun `Dockerfile uses node 20 alpine`() {
        val content = File("deploy/Dockerfile").readText()
        assertTrue(content.contains("node:20-alpine"))
    }

    @Test
    fun `Dockerfile exposes port 3000`() {
        val content = File("deploy/Dockerfile").readText()
        assertTrue(content.contains("EXPOSE 3000"))
    }

    @Test
    fun `Dockerfile runs node app js`() {
        val content = File("deploy/Dockerfile").readText()
        assertTrue(content.contains("node"))
        assertTrue(content.contains("app.js"))
    }

    @Test
    fun `docker-compose has healthcheck`() {
        val content = File("deploy/docker-compose.yml").readText()
        assertTrue(content.contains("healthcheck"))
    }

    @Test
    fun `nginx conf proxies to port 3000`() {
        val content = File("deploy/nginx.conf").readText()
        assertTrue(content.contains("127.0.0.1:3000"))
    }

    @Test
    fun `nginx conf has health endpoint`() {
        val content = File("deploy/nginx.conf").readText()
        assertTrue(content.contains("/health"))
    }
}
