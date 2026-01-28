/**
 * Expands a simple ring definition into altar_rite JSON format.
 *
 * Supported values:
 *  - "mod:item"        → { item: "mod:item" }
 *  - "#mod:tag"        → { tag: "mod:tag" }
 *  - "empty" | null    → { empty: true }
 */

global.expandAltarRing = function (ring) {
  return ring.map(e => {
    if (e === null || e === "empty") {
      return { empty: true }
    }

    if (typeof e === "string") {
      if (e.startsWith("#")) {
        return { tag: e.substring(1) }
      }
      return { item: e }
    }

    return e
  })
}

global.AR = expandAltarRing

global._DA_SACRED_WRITS = global._DA_SACRED_WRITS ?? {}

/**
 * Queue a Sacred Writ JSON to be added to the datapack.
 *
 * id: "namespace:path" (defaults to "kubejs:<id>" if no namespace)
 * json: sacred writ definition object
 */
global.addSacredWrit = function (id, json) {
  const [ns, path] = id.includes(":") ? id.split(":") : ["kubejs", id]
  global._DA_SACRED_WRITS[`${ns}:${path}`] = json
}

/**
 * Builds an altar_rite result stack for a sacred writ.
 */
global.sacredWrit = function (writId, uses = 1) {
  return {
    item: "descendedangel:sacred_writings",
    count: 1,
    nbt: `{descendedangel:{writ_id:"${writId}",uses:${uses}}}`
  }
}

ServerEvents.highPriorityData(event => {
  for (const id in global._DA_SACRED_WRITS) {
    const [ns, path] = id.split(":")
    event.addJson(`${ns}:sacred_writs/${path}`, global._DA_SACRED_WRITS[id])
  }
})