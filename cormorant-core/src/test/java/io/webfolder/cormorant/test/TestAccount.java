/**
 * cormorant - Object Storage Server
 * Copyright © 2017 WebFolder OÜ (support@webfolder.io)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.webfolder.cormorant.test;

import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.javaswift.joss.model.Container;
import org.jclouds.openstack.swift.v1.domain.Account;
import org.junit.FixMethodOrder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import okhttp3.Request;
import okhttp3.Response;

@FixMethodOrder(NAME_ASCENDING)
public class TestAccount extends TestSwift {

    @Test
    public void t1Account() {
        Account account = accountApi.get();

        assertEquals(0L, account.getContainerCount());
        assertEquals(0L, account.getObjectCount());
        assertEquals(0L, account.getBytesUsed());

        containerApi.create("container1");

        account = accountApi.get();

        assertEquals(1L, account.getContainerCount());
        assertEquals(0L, account.getObjectCount());
        assertEquals(0L, account.getBytesUsed());
    }

    @Test
    public void t2Metadata() {
        Map<String, String> metadata = accountApi.get().getMetadata();
        assertTrue(metadata.isEmpty());

        metadata = new HashMap<>();
        metadata.put("xyz", "klm");
        accountApi.updateMetadata(metadata);

        metadata = accountApi.get().getMetadata();

        assertFalse(metadata.isEmpty());

        metadata = new HashMap<>();
        metadata.put("abc", "123");

        accountApi.updateMetadata(metadata);

        metadata = accountApi.get().getMetadata();

        assertEquals(2, metadata.size());
        assertEquals("klm", metadata.get("xyz"));
        assertEquals("123", metadata.get("abc"));

        accountApi.deleteMetadata(metadata);

        metadata = accountApi.get().getMetadata();
        assertTrue(metadata.isEmpty());
    }

    @Test
    public void t3TestXmlResponse() throws IOException {
        Response response = client.newCall(new Request.Builder().url(getUrl() + "/v1/myaccount?format=xml").build()).execute();
        String content = response.body().string();
        assertTrue(content.contains("<account name=\"myaccount\"><container><name>container1</name><count>0</count><bytes>0</bytes></container></account>"));
        assertEquals("application/xml; charset=utf-8", response.header("Content-Type"));
    }

    @Test
    public void t4TestJossAccount() {
        Collection<Container> containers = jossAccount.list();
        assertFalse(containers.isEmpty());
        assertEquals(1, containers.size());
        assertEquals("container1", containers.iterator().next().getName());
    }
}
