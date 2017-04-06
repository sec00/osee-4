angular.module('MiHexample').value("UISchema", 
{
  "type": "HorizontalLayout",
  "elements": [
    {
      "type": "VerticalLayout",
      "elements": [
        {
          "type": "Control",
          "label": "Subject",
          "scope": {
            "$ref": "#/properties/subject"
          },
          "readOnly": false
        },
        {
          "type": "Control",
          "label": "Discrepancy",
          "scope": {
            "$ref": "#/properties/discrepancy"
          },
          'options': {
          'multi': true
          },
          "readOnly": false
        },
        {
          "type": "Control",
          "label": "Corrective Action",
          "scope": {
            "$ref": "#/properties/correctiveAction"
          },
          'options': {
          'multi': true
          },
          "readOnly": false
        },
        {
          "type": "Control",
          "label": "Comments/Actions/Notes",
          "scope": {
            "$ref": "#/properties/comments"
          },
          'options': {
          'multi': true
          },
          "readOnly": false
        }
      ]
    },
    {
      "type": "VerticalLayout",
      "elements": [
        {
          "type": "Control",
          "label": "Status",
          "scope": {
            "$ref": "#/properties/status"
          },
          "readOnly": false
        },
        {
          "type": "Control",
          "label": "Type",
          "scope": {
            "$ref": "#/properties/type"
          },
          "readOnly": false
        },
        {
          "type": "Control",
          "label": "Status ECD:",
          "scope": {
            "$ref": "#/properties/statusEcd"
          },
          "readOnly": false
        },
        {
          "type": "Control",
          "label": "Priority",
          "scope": {
            "$ref": "#/properties/priority"
          },
          "readOnly": false
        },
        {
          "type": "Group",
          "elements": [
            {
              "type": "HorizontalLayout",
              "elements": [
                { 
                  "type": "Control",
                  "Label": "Subsystem",
                  "scope": {
                    "$ref": "#/properties/subsystem"
                  },
                  "readOnly": false
                },
                {
                  "type": "Control",
                  "Label": "Operator Impact",
                  "scope": {
                    "$ref": "#/properties/operatorImpact"
                  },
                  "readOnly": false
                }
              ] // End Horizontal Elements
            }
          ] // End Group Elements
        },
        {
          "type": "Control",
          "label": "Where Found",
          "scope": {
            "$ref": "#/properties/whereFound"
          },
          "readOnly": false
        },
        {
          "type": "Control",
          "label": "Build Found",
          "scope": {
            "$ref": "#/properties/buildFound"
          },
          "readOnly": false
        },
        {
          "type": "Group",
          "label": "Work Around",
          "elements": [
            {
              "type": "Control",
              "label": "Crew System",
              "scope": {
                "$ref": "#/properties/isCrewSystemImpact"
              }
            },
            {
              "type": "Control",
              "label": "",
              "scope": {
                "$ref": "#/properties/natureOfImpact"
              },
              'options': {
              'multi': true
              },
            }
          ]
        },
        {
          "type": "Group",
          "label": "Work Around",
          "elements": [
            {
              "type": "Control",
              "label": "Crew System",
              "scope": {
                "$ref": "#/properties/isCrewSystemWorkAround"
              }
            },
            {
              "type": "Control",
              "label": "",
              "scope": {
                "$ref": "#/properties/workAround"
              },
              'options': {
              'multi': true
              },  
              "rule": {
                "effect": "DISABLE",
                "condition": {
                  "type": "LEAF",
                  "scope": {
                    "$ref": "#/properties/isCrewSystemWorkAround"
                  },
                  "expectedValue": true
                }
              }
            }
          ]
        }
      ]
    },
    {
      "type": "VerticalLayout",
      "elements": [
        {
          "type": "Control",
          "label": "Responsible Ipt",
          "scope": {
            "$ref": "#/properties/responsibleIpt"
          },
          "readOnly": false
        },
        {
          "type": "Control",
          "label": "Color Team",
          "scope": {
            "$ref": "#/properties/colorTeam"
          },
          "readOnly": false
        },
        {
          "type": "Group",
          "label": "Cognizant Engineers",
          "elements": [
             {
                "type": "Control",
                "label": "Engineer 1",
                "scope": {
                  "$ref": "#/properties/cognizant1"
                },
                "readOnly": false
              },
              {
                "type": "Control",
                "label": "Engineer 2",
                "scope": {
                  "$ref": "#/properties/cognizant2"
                },
                "readOnly": false
              },
              {
                "type": "Control",
                "label": "Engineer 3",
                "scope": {
                  "$ref": "#/properties/cognizant3"
                },
                "readOnly": false
              }
          ]
        }
      ]
    }
  ]
} 

);
