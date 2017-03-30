angular.module('MiHexample').value("Schema",
{
  "type": "object",
  "properties": {
    "required": [
      "subject",
      "discrepancy",
      "status",
      "type",
      "responsibleIpt",
      "colorTeam"
    ],
    "subject": {
      "type": "string"
    },
    "discrepancy": {
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
    "type": {
      "type": "string",
      "enum": [
        "OFP",
        "Supplier",
        "GFE"
      ]
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
