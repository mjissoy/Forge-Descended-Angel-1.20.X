ServerEvents.recipes(event => {
  event.custom({
    type: "descendedangel:altar_rite",

    display_type: "altar.descendedangel.consecration",
    required_halo_tier: 7,

    core: { item: "descendedangel:angel_feather" },

    ring: expandAltarRing([
      "descendedangel:void_tear",          // Top
      "empty",                             // Top Right
      "descendedangel:sacred_ore_ingot",   // Right
      "empty",                             // Bottom Right
      "descendedangel:void_tear",          // Bottom
      "empty",                             // Bottom Left
      "descendedangel:sacred_ore_ingot",   // Left
      "empty"                              // Top Left
    ]),

    result: { item: "descendedangel:real_angel_feather", count: 1 }
  })
  .id("kubejs:descendedangel/altar/sacred_angel_feather");
});