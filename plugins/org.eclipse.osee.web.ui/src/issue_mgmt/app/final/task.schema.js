angular.module('MiHexample').value("Schema",
{
  "type": "object",
  "required": [
      "subject",
      "discrepancy",
      "correctiveAction",
      "status",
      "type",
      "priority",
      "subsystem",
      "responsibleIpt",
      "colorTeam"
  ],
  "properties": {
    "subject": {
      "type": "string",
      "minLength": 3
    },
    "discrepancy": {
      "type": "string"
    },
    "correctiveAction": {
      "type": "string"
    },
    "comments": {
      "type": "string"
    },
    "isCrewSystemImpact": {
      "type": "boolean"
    },
    "isCrewSystemWorkAround": {
      "type": "boolean"
    },
    "natureOfImpact": {
      "type": "string"
    },
    "workAround": {
      "type": "string"
    },
    "status": {
      "type": "string",
      "enum": [
        "Verification",
        "Open",
        "Closed",
        "Promoted",
        "Cancelled"
      ]
    },
    "cognizant1": {
      "type": "string"
    },
    "cognizant2": {
      "type": "string"
    },
    "cognizant3": {
      "type": "string"
    },
    "type": {
      "type": "string",
      "enum": [
        "OFP",
        "Supplier",
        "GFE"
      ]
    },
    "statusEcd": {
      "type": "string",
      "format": "date"
    },
    "priority": {
      "type": "string",
      "enum": [
        "1",
        "2",
        "3",
        "4",
        "5"
      ]
    },
    "subsystem": {
      "type": "string",
      "enum": [
        "ASM",
        "NAV",
        "COMM",
        "CND",
        "BATB"
      ]
    },
    "whereFound": {
      "type": "string",
      "enum": [
        "Formal Testing",
        "Script",
        "Hot Bench",
        "Code Inspection",
        "Flight Test"
      ]
    },
    "buildFound": {
      "type": "string",
      "enum": [
        "FTB1",
        "SBVT",
        "FTB2"
      ]
    },
    "operatorImpact": {
      "type": "boolean"
    },
    "responsibleIpt": {
      "type": "string",
      "enum": [
        "Crew Systems",
        "AH6",
        "AH6i"
      ]
    },
    "colorTeam": {
      "type": "string",
      "enum": [
        "Red",
        "Bronze",
        "Gold"
      ]
    }
  }
}
);
