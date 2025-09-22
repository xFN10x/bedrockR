// custom blocks
const definitions = Blockly.common.createBlockDefinitionsFromJsonArray([
  //#region server blocks
  {
    type: "sub_block",
    tooltip: "",
    helpUrl: "",
    message0: "After entity dies %1 do %2",
    args0: [
      {
        type: "input_dummy",
        name: "TEXT",
      },
      {
        type: "input_statement",
        name: "STATEMENTS",
      },
    ],
    nextStatement: null,
    style: "server_blocks_caps",
  },
  {
    type: "sub_entitydie_block",
    tooltip: "",
    helpUrl: "",
    message0: "After entity dies %1 do %2",
    args0: [
      {
        type: "input_end_row",
        name: "TEXT",
      },
      {
        type: "input_statement",
        name: "STATEMENTS",
      },
    ],
    nextStatement: null,
    style: "server_blocks_caps",
  },
  {
    type: "send_message",
    tooltip: "",
    helpUrl: "",
    message0: "send chat %1 to all players %2",
    args0: [
      {
        type: "input_value",
        name: "VAL",
        check: "String",
      },
      {
        type: "input_dummy",
        name: "TEXT",
      },
    ],
    previousStatement: null,
    nextStatement: null,
    style: "server_blocks",
    inputsInline: true,
  },
  //#endregion
  //#region block blocks
  {
    type: "get_block_from_event",
    tooltip:
      'Returns the block involved with this event. e.g. If the event is "Item Used on Block" it returns that block. (WARNING: NOT ALL EVENTS RETURN A BLOCK)',
    helpUrl: "",
    message0: "get Block from event %1",
    args0: [
      {
        type: "input_dummy",
        name: "TEXT",
      },
    ],
    output: "Block",
    style: "block_blocks",
    inputsInline: true,
  },
  {
    type: "is_block_air",
    tooltip: "Check",
    helpUrl: "",
    message0: "is  %1 air?",
    args0: [
      {
        type: "input_value",
        name: "TARGET",
        check: "Block",
      },
    ],
    output: "Boolean",
    inputsInline: true,
    style: "block_blocks",
  },
  {
    type: "is_block_liquid",
    tooltip: "Returns if the block is something like water, or lava.",
    helpUrl: "",
    message0: "is %1 a liquid?",
    args0: [
      {
        type: "input_value",
        name: "TARGET",
        check: "Block",
      },
    ],
    output: "Boolean",
    style: "block_blocks",
  },
  {
    type: "get_block_location",
    tooltip: "Get the world location of the block",
    helpUrl: "",
    message0: "Location of block %1",
    args0: [
      {
        type: "input_value",
        name: "NAME",
        check: "Block",
      },
    ],
    output: "Vec3",
    style: "block_blocks",
    inputsInline: true,
  },
  //STOP FORGETTING COMMAS ^
  //#endregion
  //#region entity blocks
  {
    type: "get_dead_entity_from_event",
    tooltip:
      "Returns the entity, which will be dead, from the event. (Works with: ",
    helpUrl: "",
    message0: "get Dead-Entity of event %1",
    args0: [
      {
        type: "input_dummy",
        name: "TEXT",
      },
    ],
    style: "entity_blocks",
    output: "Entity",
    inputsInline: true,
  },
  {
    type: "get_id_of_entity",
    tooltip:
      "Gets an ID of an entity. This ID is the same everytime the world loads.",
    helpUrl: "",
    message0: "Unqiue ID of entity %1",
    args0: [
      {
        type: "input_value",
        name: "NAME",
        check: "Entity",
      },
    ],
    output: "String",
    style: "entity_blocks",
    inputsInline: true,
  },
  {
    type: "get_entity_bool",
    tooltip: "Gets a boolean value from the entity.",
    helpUrl: "",
    message0: "Get if entity  %1 is %2 %3",
    args0: [
      {
        type: "input_value",
        name: "TARGET",
        check: "Entity",
      },
      {
        type: "field_dropdown",
        name: "DROPDOWN",
        options: [
          ["climbing", "isClimbing"],
          ["falling", "isFalling"],
          ["in water", "isInWater"],
          ["on the ground", "isOnGround"],
          ["sleeping", "isSleeping"],
          ["is sneaking", "isSneaking"],
          ["sprinting", "isSprinting"],
          ["swimming", "isSwimming"],
        ],
      },
      {
        type: "input_dummy",
        name: "NAME",
      },
    ],
    output: "Boolean",
    style: "entity_blocks",
    inputsInline: true,
  },
  {
    type: "get_entity_valid",
    tooltip:
      "This should be checked before you start doing anything with entitys. Checks if the entity is able to be modified, or read from.",
    helpUrl: "",
    message0: "Get if entity %1 is valid",
    args0: [
      {
        type: "input_value",
        name: "TARGET",
        check: "Entity",
      },
    ],
    style: "entity_blocks",
    output: "Boolean",
  },
  {
    type: "get_player_from_event",
    tooltip: "Returns the player who caused the event.(NOT IN ALL EVENTS)",
    helpUrl: "",
    message0: "get Player of event %1",
    args0: [
      {
        type: "input_dummy",
        name: "TEXT",
      },
    ],
    output: "Player",
    style: "player_blocks",
    inputsInline: true,
  },
  //#endregion
]);

// Register the definition.
Blockly.common.defineBlocks(definitions);

const toolbox = {
  kind: "categoryToolbox",
  contents: [
    {
      kind: "category",
      name: "Server",
      categorystyle: "server_category",
      contents: [
        {
          type: "sub_entitydie_block",
          kind: "block",
        },
        {
          type: "send_message",
          kind: "block",
        },
      ],
    },
    {
      kind: "sep",
    },
    {
      kind: "category",
      name: "Block",
      categorystyle: "block_category",
      contents: [
        {
          type: "get_block_from_event",
          kind: "block",
        },
        {
          type: "is_block_air",
          kind: "block",
        },
        {
          type: "is_block_liquid",
          kind: "block",
        },
        {
          type: "get_block_location",
          kind: "block",
        },
      ],
    },
    {
      kind: "category",
      name: "Entity",
      categorystyle: "entity_category",
      contents: [
        {
          type: "get_dead_entity_from_event",
          kind: "block",
        },
        {
          type: "get_id_of_entity",
          kind: "block",
        },
        {
          type: "get_entity_bool",
          kind: "block",
        },
        {
          type: "get_entity_valid",
          kind: "block",
        },
        {
          kind: "label",
          text: "Players",
        },
        {
          type: "get_player_from_event",
          kind: "block",
        },
      ],
    },
    {
      kind: "sep",
    },

    //#region vanilla blocks
    {
      kind: "category",
      name: "Logic",
      categorystyle: "logic_category",
      contents: [
        {
          type: "controls_if",
          kind: "block",
        },
        {
          type: "logic_compare",
          kind: "block",
          fields: {
            OP: "EQ",
          },
        },
        {
          type: "logic_operation",
          kind: "block",
          fields: {
            OP: "AND",
          },
        },
        {
          type: "logic_negate",
          kind: "block",
        },
        {
          type: "logic_boolean",
          kind: "block",
          fields: {
            BOOL: "TRUE",
          },
        },
        {
          type: "logic_null",
          kind: "block",
          enabled: false,
        },
        {
          type: "logic_ternary",
          kind: "block",
        },
      ],
    },
    {
      kind: "category",
      name: "Loops",
      categorystyle: "loop_category",
      contents: [
        {
          type: "controls_repeat_ext",
          kind: "block",
          inputs: {
            TIMES: {
              shadow: {
                type: "math_number",
                fields: {
                  NUM: 10,
                },
              },
            },
          },
        },
        {
          type: "controls_repeat",
          kind: "block",
          enabled: false,
          fields: {
            TIMES: 10,
          },
        },
        {
          type: "controls_whileUntil",
          kind: "block",
          fields: {
            MODE: "WHILE",
          },
        },
        {
          type: "controls_for",
          kind: "block",
          fields: {
            VAR: {
              name: "i",
            },
          },
          inputs: {
            FROM: {
              shadow: {
                type: "math_number",
                fields: {
                  NUM: 1,
                },
              },
            },
            TO: {
              shadow: {
                type: "math_number",
                fields: {
                  NUM: 10,
                },
              },
            },
            BY: {
              shadow: {
                type: "math_number",
                fields: {
                  NUM: 1,
                },
              },
            },
          },
        },
        {
          type: "controls_forEach",
          kind: "block",
          fields: {
            VAR: {
              name: "j",
            },
          },
        },
        {
          type: "controls_flow_statements",
          kind: "block",
          enabled: false,
          fields: {
            FLOW: "BREAK",
          },
        },
      ],
    },
    {
      kind: "category",
      name: "Math",
      categorystyle: "math_category",
      contents: [
        {
          type: "math_number",
          kind: "block",
          fields: {
            NUM: 123,
          },
        },
        {
          type: "math_arithmetic",
          kind: "block",
          fields: {
            OP: "ADD",
          },
          inputs: {
            A: {
              shadow: {
                type: "math_number",
                fields: {
                  NUM: 1,
                },
              },
            },
            B: {
              shadow: {
                type: "math_number",
                fields: {
                  NUM: 1,
                },
              },
            },
          },
        },
        {
          type: "math_single",
          kind: "block",
          fields: {
            OP: "ROOT",
          },
          inputs: {
            NUM: {
              shadow: {
                type: "math_number",
                fields: {
                  NUM: 9,
                },
              },
            },
          },
        },
        {
          type: "math_trig",
          kind: "block",
          fields: {
            OP: "SIN",
          },
          inputs: {
            NUM: {
              shadow: {
                type: "math_number",
                fields: {
                  NUM: 45,
                },
              },
            },
          },
        },
        {
          type: "math_constant",
          kind: "block",
          fields: {
            CONSTANT: "PI",
          },
        },
        {
          type: "math_number_property",
          kind: "block",
          fields: {
            PROPERTY: "EVEN",
          },
          inputs: {
            NUMBER_TO_CHECK: {
              shadow: {
                type: "math_number",
                fields: {
                  NUM: 0,
                },
              },
            },
          },
        },
        {
          type: "math_round",
          kind: "block",
          fields: {
            OP: "ROUND",
          },
          inputs: {
            NUM: {
              shadow: {
                type: "math_number",
                fields: {
                  NUM: 3.1,
                },
              },
            },
          },
        },
        {
          type: "math_on_list",
          kind: "block",
          fields: {
            OP: "SUM",
          },
        },
        {
          type: "math_modulo",
          kind: "block",
          inputs: {
            DIVIDEND: {
              shadow: {
                type: "math_number",
                fields: {
                  NUM: 64,
                },
              },
            },
            DIVISOR: {
              shadow: {
                type: "math_number",
                fields: {
                  NUM: 10,
                },
              },
            },
          },
        },
        {
          type: "math_constrain",
          kind: "block",
          inputs: {
            VALUE: {
              shadow: {
                type: "math_number",
                fields: {
                  NUM: 50,
                },
              },
            },
            LOW: {
              shadow: {
                type: "math_number",
                fields: {
                  NUM: 1,
                },
              },
            },
            HIGH: {
              shadow: {
                type: "math_number",
                fields: {
                  NUM: 100,
                },
              },
            },
          },
        },
        {
          type: "math_random_int",
          kind: "block",
          inputs: {
            FROM: {
              shadow: {
                type: "math_number",
                fields: {
                  NUM: 1,
                },
              },
            },
            TO: {
              shadow: {
                type: "math_number",
                fields: {
                  NUM: 100,
                },
              },
            },
          },
        },
        {
          type: "math_random_float",
          kind: "block",
        },
        {
          type: "math_atan2",
          kind: "block",
          inputs: {
            X: {
              shadow: {
                type: "math_number",
                fields: {
                  NUM: 1,
                },
              },
            },
            Y: {
              shadow: {
                type: "math_number",
                fields: {
                  NUM: 1,
                },
              },
            },
          },
        },
      ],
    },
    {
      kind: "category",
      name: "Text",
      categorystyle: "text_category",
      contents: [
        {
          type: "text",
          kind: "block",
          fields: {
            TEXT: "",
          },
        },
        {
          type: "text_join",
          kind: "block",
        },
        {
          type: "text_append",
          kind: "block",
          fields: {
            name: "item",
          },
          inputs: {
            TEXT: {
              shadow: {
                type: "text",
                fields: {
                  TEXT: "",
                },
              },
            },
          },
        },
        {
          type: "text_length",
          kind: "block",
          inputs: {
            VALUE: {
              shadow: {
                type: "text",
                fields: {
                  TEXT: "abc",
                },
              },
            },
          },
        },
        {
          type: "text_isEmpty",
          kind: "block",
          inputs: {
            VALUE: {
              shadow: {
                type: "text",
                fields: {
                  TEXT: "",
                },
              },
            },
          },
        },
        {
          type: "text_indexOf",
          kind: "block",
          fields: {
            END: "FIRST",
          },
          inputs: {
            VALUE: {
              block: {
                type: "variables_get",
                fields: {
                  VAR: {
                    name: "text",
                  },
                },
              },
            },
            FIND: {
              shadow: {
                type: "text",
                fields: {
                  TEXT: "abc",
                },
              },
            },
          },
        },
        {
          type: "text_charAt",
          kind: "block",
          fields: {
            WHERE: "FROM_START",
          },
          inputs: {
            VALUE: {
              block: {
                type: "variables_get",
                fields: {
                  VAR: {
                    name: "text",
                  },
                },
              },
            },
          },
        },
        {
          type: "text_getSubstring",
          kind: "block",
          fields: {
            WHERE1: "FROM_START",
            WHERE2: "FROM_START",
          },
          inputs: {
            STRING: {
              block: {
                type: "variables_get",
                fields: {
                  VAR: {
                    name: "text",
                  },
                },
              },
            },
          },
        },
        {
          type: "text_changeCase",
          kind: "block",
          fields: {
            CASE: "UPPERCASE",
          },
          inputs: {
            TEXT: {
              shadow: {
                type: "text",
                fields: {
                  TEXT: "abc",
                },
              },
            },
          },
        },
        {
          type: "text_trim",
          kind: "block",
          fields: {
            MODE: "BOTH",
          },
          inputs: {
            TEXT: {
              shadow: {
                type: "text",
                fields: {
                  TEXT: "abc",
                },
              },
            },
          },
        },
        {
          type: "text_count",
          kind: "block",
          inputs: {
            SUB: {
              shadow: {
                type: "text",
                fields: {
                  TEXT: "",
                },
              },
            },
            TEXT: {
              shadow: {
                type: "text",
                fields: {
                  TEXT: "",
                },
              },
            },
          },
        },
        {
          type: "text_replace",
          kind: "block",
          inputs: {
            FROM: {
              shadow: {
                type: "text",
                fields: {
                  TEXT: "",
                },
              },
            },
            TO: {
              shadow: {
                type: "text",
                fields: {
                  TEXT: "",
                },
              },
            },
            TEXT: {
              shadow: {
                type: "text",
                fields: {
                  TEXT: "",
                },
              },
            },
          },
        },
        {
          type: "text_reverse",
          kind: "block",
          inputs: {
            TEXT: {
              shadow: {
                type: "text",
                fields: {
                  TEXT: "",
                },
              },
            },
          },
        },

        {
          type: "text_print",
          kind: "block",
          inputs: {
            TEXT: {
              shadow: {
                type: "text",
                fields: {
                  TEXT: "abc",
                },
              },
            },
          },
        },
        {
          type: "text_prompt_ext",
          kind: "block",
          fields: {
            TYPE: "TEXT",
          },
          inputs: {
            TEXT: {
              shadow: {
                type: "text",
                fields: {
                  TEXT: "abc",
                },
              },
            },
          },
        },
      ],
    },
    {
      kind: "category",
      name: "Lists",
      categorystyle: "list_category",
      contents: [
        {
          type: "lists_create_with",
          kind: "block",
        },
        {
          type: "lists_create_with",
          kind: "block",
        },
        {
          type: "lists_repeat",
          kind: "block",
          inputs: {
            NUM: {
              shadow: {
                type: "math_number",
                fields: {
                  NUM: 5,
                },
              },
            },
          },
        },
        {
          type: "lists_length",
          kind: "block",
        },
        {
          type: "lists_isEmpty",
          kind: "block",
        },
        {
          type: "lists_indexOf",
          kind: "block",

          fields: {
            END: "FIRST",
          },
          inputs: {
            VALUE: {
              block: {
                type: "variables_get",
                fields: {
                  VAR: {
                    name: "list",
                  },
                },
              },
            },
          },
        },
        {
          type: "lists_getIndex",
          kind: "block",
          fields: {
            MODE: "GET",
            WHERE: "FROM_START",
          },
          inputs: {
            VALUE: {
              block: {
                type: "variables_get",
                fields: {
                  VAR: {
                    name: "list",
                  },
                },
              },
            },
          },
        },
        {
          type: "lists_setIndex",
          kind: "block",
          fields: {
            MODE: "SET",
            WHERE: "FROM_START",
          },
          inputs: {
            LIST: {
              block: {
                type: "variables_get",
                fields: {
                  VAR: {
                    name: "list",
                  },
                },
              },
            },
          },
        },
        {
          type: "lists_getSublist",
          kind: "block",
          fields: {
            WHERE1: "FROM_START",
            WHERE2: "FROM_START",
          },
          inputs: {
            LIST: {
              block: {
                type: "variables_get",
                fields: {
                  VAR: {
                    name: "list",
                  },
                },
              },
            },
          },
        },
        {
          type: "lists_split",
          kind: "block",

          fields: {
            MODE: "SPLIT",
          },
          inputs: {
            DELIM: {
              shadow: {
                type: "text",
                fields: {
                  TEXT: ",",
                },
              },
            },
          },
        },
        {
          type: "lists_sort",
          kind: "block",

          fields: {
            TYPE: "NUMERIC",
            DIRECTION: "1",
          },
        },
        {
          type: "lists_reverse",
          kind: "block",
        },
      ],
    },

    //#endregion
  ],
};

//load the injection
const workspace = Blockly.inject("blocklyDiv", {
  toolbox: toolbox,
  trashcan: true,
  renderer: "Thrasos",
});

const bedrockRDark = Blockly.Theme.defineTheme("bedrockRDark", {
  base: Blockly.ThemeDark,
  startHats: true,
  categoryStyles: {
    server_category: {
      colour: "#6d6d6d",
    },
    block_category: {
      colour: "#00a028",
    },
    entity_category: {
      colour: "#004a98",
    },
  },
  blockStyles: {
    //#region vanilla
    colour_blocks: { colourPrimary: "20" },
    list_blocks: { colourPrimary: "260" },
    logic_blocks: { colourPrimary: "210" },
    loop_blocks: { colourPrimary: "120" },
    math_blocks: { colourPrimary: "230" },
    procedure_blocks: { colourPrimary: "290" },
    text_blocks: { colourPrimary: "160" },
    variable_blocks: { colourPrimary: "330" },
    variable_dynamic_blocks: { colourPrimary: "310" },
    hat_blocks: { colourPrimary: "330", hat: "cap" },
    //#endregion
    server_blocks: {
      colourPrimary: "#6d6d6d",
      colourSecondary: "#2b2b2b",
      colourTertiary: "#e8e8e8",
    },
    server_blocks_caps: {
      colourPrimary: "#6d6d6d",
      colourSecondary: "#2b2b2b",
      colourTertiary: "#e8e8e8",
      hat: "cap",
    },
    //confusing name
    block_blocks: {
      colourPrimary: "#00a028",
      colourSecondary: "#006218",
      colourTertiary: "#8aa190",
    },
    entity_blocks: {
      colourPrimary: "#004a98",
      colourSecondary: "#002d5d",
      colourTertiary: "#007bff",
    },
    player_blocks: {
      colourPrimary: "#2f79c8",
      colourSecondary: "#1d528b",
      colourTertiary: "#4e76a0",
    },
  },
  componentStyles: {
    workspaceBackgroundColour: "#202020",
    toolboxBackgroundColour: "#272727",
    toolboxForegroundColour: "#fff",
    flyoutBackgroundColour: "#252526",
    flyoutForegroundColour: "#ccc",
    flyoutOpacity: 1,
    scrollbarColour: "#797979",
    insertionMarkerColour: "#fff",
    insertionMarkerOpacity: 0.3,
    scrollbarOpacity: 0.4,
    cursorColour: "#d0d0d0",
    blackBackground: "#333",
  },
});
workspace.setTheme(bedrockRDark);

javascript.javascriptGenerator.forBlock['get_entity_valid'] = function(
  block,
  generator
) {
  const value_target = generator.valueToCode(block, 'TARGET', javascript.Order.NONE);

  const code = `${value_target}.isValid`;
  return [code, javascript.Order.NONE];
}

javascript.javascriptGenerator.forBlock["get_entity_bool"] = function (
  block,
  generator
) {
  const value_target = generator.valueToCode(
    block,
    "TARGET",
    javascript.Order.NONE
  );

  const dropdown_dropdown = block.getFieldValue("DROPDOWN");

  const code = `${value_target}.${dropdown_dropdown}`;
  return [code, javascript.Order.NONE];
};

javascript.javascriptGenerator.forBlock["get_id_of_entity"] = function (
  block,
  generator
) {
  const value_name = generator.valueToCode(
    block,
    "NAME",
    javascript.Order.NONE
  );

  const code = `${value_name}.id`;
  return [code, javascript.Order.NONE];
};

javascript.javascriptGenerator.forBlock["get_dead_entity_from_event"] =
  function (block, generator) {
    const code = "deadEntity";
    return [code, javascript.Order.NONE];
  };

javascript.javascriptGenerator.forBlock["get_block_location"] = function (
  block,
  generator
) {
  const value_name = generator.valueToCode(
    block,
    "NAME",
    javascript.Order.NONE
  );

  const code = `${value_name}.location`;

  console.log(javascript.Order);

  return [code, javascript.Order.NONE];
};

javascript.javascriptGenerator.forBlock["is_block_liquid"] = function (
  block,
  generator
) {
  const value_target = generator.valueToCode(
    block,
    "TARGET",
    javascript.Order.NONE
  );

  const code = `${value_target}.isLiquid`;
  return [code, javascript.Order.NONE];
};

javascript.javascriptGenerator.forBlock["get_block_from_event"] = function (
  block,
  generator
) {
  const code = "block";
  return [code, javascript.Order.NONE];
};

javascript.javascriptGenerator.forBlock["get_player_from_event"] = function (
  block,
  generator
) {
  const code = "source";
  return [code, javascript.Order.NONE];
};

javascript.javascriptGenerator.forBlock["is_block_air"] = function (
  block,
  generator
) {
  const value_target = generator.valueToCode(
    block,
    "TARGET",
    javascript.Order.NONE
  );
  const code = `${value_target}.isAir`;
  return [code, javascript.Order.NONE];
};

//put this here for compatibility
javascript.javascriptGenerator.forBlock["sub_block"] = function (
  block,
  generator
) {
  const statement_statements = generator.statementToCode(block, "STATEMENTS");
  const code = `world.afterEvents.entityDie.subscribe(event => {\n  const {damageSource, deadEntity} = event;\n${statement_statements}})`;
  return code;
};

javascript.javascriptGenerator.forBlock["sub_entitydie_block"] = function (
  block,
  generator
) {
  const statement_statements = generator.statementToCode(block, "STATEMENTS");
  const code = `world.afterEvents.entityDie.subscribe(event => {\n  const {damageSource, deadEntity} = event;\n${statement_statements}})`;
  return code;
};

javascript.javascriptGenerator.forBlock["send_message"] = function (
  block,
  generator
) {
  const value_val = generator.valueToCode(block, "VAL", javascript.Order.NONE);

  const code = `world.sendMessage(${value_val});`;
  return code;
};

Blockly.getMainWorkspace().addChangeListener(() => {
  if (workspace.isDragging()) return; // Don't update while changes are happening.

  rblockly.updatePreview(
    javascript.javascriptGenerator.workspaceToCode(Blockly.getMainWorkspace())
  );
});

function getSaveJSON() {
  return Blockly.serialization.workspaces.save(Blockly.getMainWorkspace());
}
function loadJson(json) {
  Blockly.serialization.workspaces.load(
    JSON.parse(json),
    Blockly.getMainWorkspace()
  );
}

let _renderTimeout = null;
// do stuff to make rendering work
function fixRendering(delay = 120) {
  // debounce
  if (_renderTimeout) clearTimeout(_renderTimeout);
  _renderTimeout = setTimeout(() => {
    try {
      // ensure the Blockly svg fits its container
      Blockly.svgResize(workspace);

      // "render" call on workspace helps re-layout the toolbox/blocks
      if (typeof workspace.render === "function") workspace.render();

      // re-init & render every block's svg if necessary
      workspace.getAllBlocks(false).forEach((b) => {
        // initSvg only if not already initialized
        if (typeof b.initSvg === "function" && !b.svg_) {
          try {
            b.initSvg();
          } catch (e) {
          }
        }
        if (typeof b.render === "function") {
          try {
            b.render();
          } catch (e) {
          
          }
        }
      });

      // also trigger a resize event in the page (some engines rely on it)
      try {
        window.dispatchEvent(new Event("resize"));
      } catch (e) {}
    } catch (e) {
      console.error("fixRendering error", e);
    }
    _renderTimeout = null;
  }, delay);
}

fixRendering(150)