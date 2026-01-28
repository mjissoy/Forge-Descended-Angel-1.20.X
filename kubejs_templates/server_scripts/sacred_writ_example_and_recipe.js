// Define the writ
addSacredWrit("kubejs:mass_summoning", {
  type: "descendedangel:spawn_entity",
  entities: [
    { id: "minecraft:villager", weight: 0.7 },
    { id: "minecraft:iron_golem", weight: 0.1 },
    "minecraft:wandering_trader"
  ],
  count: 10,
  radius: 4.0,

  display: {
    name: "Scripture of Mass Summoning",
    tooltip: [
      "Summons the inhabitants of a distant village."
    ]
  }
})

// Add it to a recipe
ServerEvents.recipes(event => {
  event.custom({
    type: "descendedangel:altar_rite",
    display_type: "altar.descendedangel.imbuement",
    required_halo_tier: 3,

    core: { item: "minecraft:book" },

    ring: expandAltarRing([
      "minecraft:emerald_block",
      "empty",
      "descendedangel:void_tear",
      "empty",
      "minecraft:carrot",
      "empty",
      "descendedangel:void_tear",
      "empty"
    ]),

    result: sacredWrit("kubejs:mass_summoning", 1)
  })
  .id("kubejs:descendedangel/altar/mass_summoning")
})
