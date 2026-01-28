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