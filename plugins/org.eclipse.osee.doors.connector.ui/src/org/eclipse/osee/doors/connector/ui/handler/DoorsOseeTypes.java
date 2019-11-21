/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.doors.connector.ui.handler;

import static org.eclipse.osee.doors.connector.ui.handler.DoorsTypeTokenProvider.doors;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeString;

/**
 * Class to create Doors types
 *
 * @author Chandan Bandemutt
 */
public interface DoorsOseeTypes {

   /**
    * Doors types
    */

   // @formatter:off
   AttributeTypeString DoorReqDatabaseName = doors.createString(8197L, "Door Req Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DoorReqId = doors.createString(5764607523034243074L, "Door Req Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DoorReqModuleName = doors.createString(5764607523034243076L, "Door Req Module Name", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DoorReqName = doors.createString(5764607523034243075L, "Door Req Name", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DoorReqUrl = doors.createString(8198L, "Door Req URL", MediaType.TEXT_PLAIN, "");

   public static final ArtifactTypeToken DoorsRequirement = ArtifactTypeToken.valueOf(5764607523034243073L, "Doors Requirement");
   // @formatter:on

}