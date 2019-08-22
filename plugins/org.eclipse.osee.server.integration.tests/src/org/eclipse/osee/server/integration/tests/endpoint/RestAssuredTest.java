/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.server.integration.tests.endpoint;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.junit.Test;

public class RestAssuredTest {

   @Test
   public void testOrcsApplicability() {
      given().port(OseeClient.getPort()).header(OseeClient.OSEE_ACCOUNT_ID, "61106791").when().get(
         "/orcs/branch/3/applic/artifact/425075333").then().body("id", equalTo("1"));
   }
}