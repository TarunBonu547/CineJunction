(function () {
  window.CineJunction = window.CineJunction || {};

  const mockMovies = [
    {
      id: "the-last-horizon",
      title: "The Last Horizon",
      type: "movie",
      tagline: "A signal at the edge of the ocean rewrites the map of memory.",
      year: 2026,
      runtime: "2h 12m",
      genres: ["Sci-Fi", "Drama", "Mystery"],
      language: "English",
      ageRating: "PG-13",
      imdbRating: "8.7",
      cjRating: "9.2",
      popularityScore: "91%",
      director: "Mina Kade",
      writers: "Ari Vale, Lena Ortiz",
      producers: "Nadia Sato, Daniel Quill",
      productionCompany: "Northlight Studios",
      budget: "$38M",
      boxOffice: "$142M",
      country: "United States",
      languages: "English, Japanese",
      awards: "Berlin International Film Festival – Official Selection",
      filmingLocations: "Mendocino, Kyoto",
      posterUrl: "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=800&q=80",
      backdropUrl: "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1800&q=80",
      description: "A gifted navigator returns to the edge of the Pacific to uncover a signal that could alter the fate of a collapsing city.",
      synopsis: "A gifted navigator returns to the edge of the Pacific to uncover a signal that could alter the fate of a collapsing city. As the inquiry deepens, the mission becomes less about survival than about whether the truth is worth preserving at all.",
      cast: [
        { name: "Mina Kade", role: "Director", characterName: "", imageUrl: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=500&q=80" },
        { name: "Eli Mercer", role: "Lead Actor", characterName: "Noah Voss", imageUrl: "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=500&q=80" },
        { name: "Lina Ford", role: "Composer", characterName: "Original Score", imageUrl: "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=500&q=80" },
        { name: "Jonah Stein", role: "Cinematographer", characterName: "Visual Language", imageUrl: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=500&q=80" }
      ],
      mediaGallery: [
        { type: "image", title: "Official Poster", src: "https://images.unsplash.com/photo-1517602302552-471fe67acf66?auto=format&fit=crop&w=1200&q=80" },
        { type: "image", title: "Production Still", src: "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1200&q=80" },
        { type: "video", title: "Official Trailer", src: "https://www.youtube.com/embed/ScMzIvxBSi4?autoplay=1" }
      ],
      streaming: [
        { name: "Netflix", status: "Available for streaming" },
        { name: "Prime Video", status: "Rental soon" },
        { name: "Disney+", status: "Coming later this season" },
        { name: "Apple TV", status: "Purchase option" }
      ],
      reviews: [
        { author: "Nadia L.", type: "user", rating: 5, helpful: 214, text: "A devastating, elegant story that understands the loneliness of longing and the cost of truth. It never overexplains itself, which makes the ending land harder.", spoiler: false },
        { author: "The Film Ledger", type: "critic", rating: 4, helpful: 92, text: "The pacing is deliberate, the imagery immaculate, and the emotional payoffs are earned. The only weakness is a slightly over-stylized final act.", spoiler: true }
      ]
    },
    {
      id: "midnight-tides",
      title: "Midnight Tides",
      type: "movie",
      tagline: "A citywide blackout reveals a hidden pattern in the night sky.",
      year: 2025,
      runtime: "1h 50m",
      genres: ["Thriller", "Mystery"],
      language: "English",
      ageRating: "R",
      imdbRating: "8.1",
      cjRating: "8.8",
      popularityScore: "86%",
      director: "Marcus Cole",
      writers: "Marcus Cole, Sarah Chen",
      producers: "Lena Ortiz",
      productionCompany: "Aether Pictures",
      budget: "$25M",
      boxOffice: "$98M",
      country: "Canada",
      languages: "English",
      awards: "Toronto Film Festival — Best Screenplay Winner",
      filmingLocations: "Vancouver",
      posterUrl: "https://images.unsplash.com/photo-1517602302552-471fe67acf66?auto=format&fit=crop&w=800&q=80",
      backdropUrl: "https://images.unsplash.com/photo-1517602302552-471fe67acf66?auto=format&fit=crop&w=1800&q=80",
      description: "A detective uncovers astronomical alignments during a grid failure in a coastal city.",
      synopsis: "During a series of coordinated municipal grid failures, a veteran inspector uncovers a terrifying connection between a cold missing-persons case and a mysterious alignment visible only when the city's neon haze fades.",
      cast: [
        { name: "Marcus Cole", role: "Director", characterName: "", imageUrl: "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=500&q=80" },
        { name: "Kiera Knight", role: "Lead Actress", characterName: "Det. Vance", imageUrl: "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=500&q=80" }
      ],
      mediaGallery: [
        { type: "image", title: "Key Art", src: "https://images.unsplash.com/photo-1517602302552-471fe67acf66?auto=format&fit=crop&w=1200&q=80" }
      ],
      streaming: [
        { name: "Netflix", status: "Rental soon" },
        { name: "Prime Video", status: "Available now" }
      ],
      reviews: [
        { author: "Tyler V.", type: "user", rating: 4, helpful: 84, text: "Suspenseful from start to finish. The cinematography during the blackout scenes is breathtaking.", spoiler: false }
      ]
    },
    {
      id: "velvet-silence",
      title: "Velvet Silence",
      type: "movie",
      tagline: "An elegant portrait of memory, grief, and slow-burning desire.",
      year: 2024,
      runtime: "1h 58m",
      genres: ["Drama", "Romance"],
      language: "French",
      ageRating: "PG-13",
      imdbRating: "7.9",
      cjRating: "8.3",
      popularityScore: "78%",
      director: "Camille Dubois",
      writers: "Camille Dubois",
      producers: "Pierre Gaultier",
      productionCompany: "Lumière Films",
      budget: "$12M",
      boxOffice: "$45M",
      country: "France",
      languages: "French",
      awards: "Cannes Film Festival — Jury Prize Winner",
      filmingLocations: "Provence",
      posterUrl: "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=800&q=80",
      backdropUrl: "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=1800&q=80",
      description: "A blind pianist returns to his family's rural French estate to confront a secret from the summer of 1998.",
      synopsis: "An elegant, deliberate portrait of memory and grief. A blind musician retires to his childhood estate in southern France, only to receive a guest who brings news of a love thought lost to history.",
      cast: [
        { name: "Jean Reno", role: "Pianist", characterName: "Henri", imageUrl: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=500&q=80" }
      ],
      mediaGallery: [
        { type: "image", title: "Still 1", src: "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=1200&q=80" }
      ],
      streaming: [
        { name: "Apple TV", status: "Purchase option" }
      ],
      reviews: [
        { author: "Marie C.", type: "critic", rating: 5, helpful: 140, text: "A quiet masterpiece. Camille Dubois shows incredible emotional maturity in directing.", spoiler: false }
      ]
    },
    {
      id: "aether-bloom",
      title: "Aether Bloom",
      type: "anime",
      tagline: "A luminous journey through ruin, ritual, and renewal.",
      year: 2026,
      runtime: "2h 06m",
      genres: ["Fantasy", "Anime", "Action"],
      language: "Japanese",
      ageRating: "PG",
      imdbRating: "8.6",
      cjRating: "9.0",
      popularityScore: "89%",
      director: "Kenji Sato",
      writers: "Kenji Sato, Yuko Tanaka",
      producers: "Satoshi Kon",
      productionCompany: "Studio Horizon",
      budget: "$15M",
      boxOffice: "$88M",
      country: "Japan",
      languages: "Japanese, English",
      awards: "Tokyo Anime Awards — Best Visuals",
      filmingLocations: "Kyoto (Design Base)",
      posterUrl: "https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=800&q=80",
      backdropUrl: "https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=1800&q=80",
      description: "A village shrine maiden explores a decaying techno-magical forest to stop a blight.",
      synopsis: "In a world where magic and machinery decay together, a young shrine maiden embarks on a quest to restore the ancient Aether Tree before its final seed fades into dust.",
      cast: [
        { name: "Aoi Yuki", role: "Voice Actress", characterName: "Miko", imageUrl: "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=500&q=80" }
      ],
      mediaGallery: [
        { type: "image", title: "Concepts", src: "https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=1200&q=80" }
      ],
      streaming: [
        { name: "Crunchyroll", status: "Available now" }
      ],
      reviews: [
        { author: "Otaku review", type: "user", rating: 5, helpful: 312, text: "Visually stunning. Studio Horizon does not miss. The soundtrack is absolute perfection.", spoiler: false }
      ]
    },
    {
      id: "glass-meridian",
      title: "Glass Meridian",
      type: "movie",
      tagline: "A fractured map reveals a city that refuses to be forgotten.",
      year: 2026,
      runtime: "1h 56m",
      genres: ["Mystery", "Thriller"],
      language: "English",
      ageRating: "R",
      imdbRating: "8.3",
      cjRating: "8.9",
      popularityScore: "90%",
      director: "David Finch",
      writers: "Robert Towne",
      producers: "Nadia Sato",
      productionCompany: "Meridian Labs",
      budget: "$42M",
      boxOffice: "$185M",
      country: "United Kingdom",
      languages: "English",
      awards: "BAFTA Award Nominee — Cinematography",
      filmingLocations: "London",
      posterUrl: "https://images.unsplash.com/photo-1517602302552-471fe67acf66?auto=format&fit=crop&w=800&q=80",
      backdropUrl: "https://images.unsplash.com/photo-1517602302552-471fe67acf66?auto=format&fit=crop&w=1800&q=80",
      description: "A cartographer uncovers hidden borders and passages in historical London maps.",
      synopsis: "A specialized map restorer at the British Museum discovers hidden coordinate lines embedded in 18th-century layouts that match modern subterranean utility tunnels.",
      cast: [
        { name: "Cillian Murphy", role: "Cartographer", characterName: "Arthur", imageUrl: "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=500&q=80" }
      ],
      mediaGallery: [
        { type: "image", title: "Subway scene", src: "https://images.unsplash.com/photo-1517602302552-471fe67acf66?auto=format&fit=crop&w=1200&q=80" }
      ],
      streaming: [
        { name: "Prime Video", status: "Streaming now" }
      ],
      reviews: [
        { author: "Cinema Review", type: "critic", rating: 4, helpful: 110, text: "A cold, precise, and gripping thriller. Cillian Murphy is spectacular as the obsessive cartographer.", spoiler: false }
      ]
    },
    {
      id: "northbound-light",
      title: "Northbound Light",
      type: "movie",
      tagline: "A road film with a quietly devastating emotional core.",
      year: 2025,
      runtime: "1h 48m",
      genres: ["Adventure", "Drama"],
      language: "Swedish",
      ageRating: "PG",
      imdbRating: "8.1",
      cjRating: "8.7",
      popularityScore: "82%",
      director: "Anders Lind",
      writers: "Anders Lind",
      producers: "Nils Borg",
      productionCompany: "Svensk Film",
      budget: "$8M",
      boxOffice: "$22M",
      country: "Sweden",
      languages: "Swedish",
      awards: "Nordic Council Film Prize",
      filmingLocations: "Lapland",
      posterUrl: "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=800&q=80",
      backdropUrl: "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=1800&q=80",
      description: "An estranged father and daughter travel to the northern tip of Sweden to catch the aurora borealis.",
      synopsis: "An estranged father and his daughter take a slow, dialogue-heavy road trip across wintry Sweden to fulfill his final promise of seeing the northern lights together.",
      cast: [
        { name: "Stellan Skarsgard", role: "Father", characterName: "Lars", imageUrl: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=500&q=80" }
      ],
      mediaGallery: [
        { type: "image", title: "Lapland Still", src: "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=1200&q=80" }
      ],
      streaming: [
        { name: "MUBI", status: "Streaming now" }
      ],
      reviews: [
        { author: "Elin S.", type: "user", rating: 4, helpful: 56, text: "A quiet, gorgeous movie. The landscapes are beautiful, and the emotional climax is incredibly moving.", spoiler: false }
      ]
    },
    {
      id: "kintsugi",
      title: "Kintsugi",
      type: "movie",
      tagline: "A quiet masterpiece about repair, memory, and impermanence.",
      year: 2024,
      runtime: "2h 02m",
      genres: ["Drama"],
      language: "Japanese",
      ageRating: "G",
      imdbRating: "8.6",
      cjRating: "9.1",
      popularityScore: "85%",
      director: "Hiroshi Ando",
      writers: "Hiroshi Ando",
      producers: "Yuki Sato",
      productionCompany: "Tokyo Cine",
      budget: "$5M",
      boxOffice: "$18M",
      country: "Japan",
      languages: "Japanese",
      awards: "Venice Critics Week Winner",
      filmingLocations: "Kyoto, Kanazawa",
      posterUrl: "https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=800&q=80",
      backdropUrl: "https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=1800&q=80",
      description: "An aging lacquerware master teaches an apprentice the art of repairing broken pottery with gold.",
      synopsis: "Focusing on the quiet daily life of a pottery master, this film reveals a deep story of forgiveness and second chances through the metaphor of lacquer repair.",
      cast: [
        { name: "Ken Watanabe", role: "Master", characterName: "Takashi", imageUrl: "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=500&q=80" }
      ],
      mediaGallery: [
        { type: "image", title: "Workshop Still", src: "https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=1200&q=80" }
      ],
      streaming: [
        { name: "Criterion Channel", status: "Available now" }
      ],
      reviews: [
        { author: "K. Tanaka", type: "user", rating: 5, helpful: 98, text: "A slow, deep film that rewards patient viewers. Highly recommended.", spoiler: false }
      ]
    },
    {
      id: "ruin-rose",
      title: "Ruin & Rose",
      type: "movie",
      tagline: "A luminous story of trust, loss, and second chances.",
      year: 2026,
      runtime: "2h 04m",
      genres: ["Romance", "Drama"],
      language: "English",
      ageRating: "R",
      imdbRating: "8.9",
      cjRating: "9.3",
      popularityScore: "95%",
      director: "Clara Vance",
      writers: "Clara Vance",
      producers: "Daniel Quill",
      productionCompany: "Rosebud Pictures",
      budget: "$30M",
      boxOffice: "$120M",
      country: "Ireland",
      languages: "English",
      awards: "Golden Globe Winner — Best Drama",
      filmingLocations: "Dublin",
      posterUrl: "https://images.unsplash.com/photo-1517602302552-471fe67acf66?auto=format&fit=crop&w=800&q=80",
      backdropUrl: "https://images.unsplash.com/photo-1517602302552-471fe67acf66?auto=format&fit=crop&w=1800&q=80",
      description: "Two rival historical researchers fall in love while translating letters of the Easter Rising.",
      synopsis: "A soaring romance set in academic archives, detailing how a shared historical search brings two lonely intellectuals together in the rainy streets of Dublin.",
      cast: [
        { name: "Saoirse Ronan", role: "Historian", characterName: "Fiona", imageUrl: "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=500&q=80" }
      ],
      mediaGallery: [
        { type: "image", title: "Archive scene", src: "https://images.unsplash.com/photo-1517602302552-471fe67acf66?auto=format&fit=crop&w=1200&q=80" }
      ],
      streaming: [
        { name: "HBO Max", status: "Streaming now" }
      ],
      reviews: [
        { author: "Liam O.", type: "critic", rating: 5, helpful: 205, text: "Simply magnificent. The emotional chemistry is electric.", spoiler: false }
      ]
    },
    {
      id: "quiet-protocol",
      title: "Quiet Protocol",
      type: "movie",
      tagline: "A disciplined intelligence officer uncovers a chain of betrayals.",
      year: 2025,
      runtime: "1h 52m",
      genres: ["Thriller", "Action"],
      language: "German",
      ageRating: "R",
      imdbRating: "8.5",
      cjRating: "8.9",
      popularityScore: "87%",
      director: "Fritz Lang",
      writers: "Hans Muller",
      producers: "Nadia Sato",
      productionCompany: "Berlin Noir",
      budget: "$20M",
      boxOffice: "$75M",
      country: "Germany",
      languages: "German, English",
      awards: "German Film Awards — Outstanding Feature",
      filmingLocations: "Berlin, Geneva",
      posterUrl: "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=800&q=80",
      backdropUrl: "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=1800&q=80",
      description: "An analyst discovers secret communication channels within his own state agency.",
      synopsis: "An analyst for the Federal Intelligence Service in Berlin spots an anomaly in cryptographic traffic that points directly to a high-ranking mole within the department.",
      cast: [
        { name: "Mads Mikkelsen", role: "Inspector", characterName: "Klaus", imageUrl: "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=500&q=80" }
      ],
      mediaGallery: [
        { type: "image", title: "Office scene", src: "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=1200&q=80" }
      ],
      streaming: [
        { name: "Prime Video", status: "Available now" }
      ],
      reviews: [
        { author: "Dieter K.", type: "user", rating: 4, helpful: 77, text: "A cold war thriller in a modern setting. Very tense.", spoiler: false }
      ]
    },
    {
      id: "ashes-in-motion",
      title: "Ashes in Motion",
      type: "movie",
      tagline: "The final orbit of a lost generation pulse through the stars.",
      year: 2026,
      runtime: "2h 10m",
      genres: ["Sci-Fi", "Drama"],
      language: "English",
      ageRating: "PG-13",
      imdbRating: "8.2",
      cjRating: "8.6",
      popularityScore: "88%",
      director: "Mina Kade",
      writers: "Ari Vale",
      producers: "Nadia Sato",
      productionCompany: "Northlight Studios",
      budget: "$35M",
      boxOffice: "$105M",
      country: "United States",
      languages: "English",
      awards: "Hugo Award Winner — Best Narrative",
      filmingLocations: "Utah Desert",
      posterUrl: "https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=800&q=80",
      backdropUrl: "https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=1800&q=80",
      description: "A colony ship orbiting a dead star tries to keep hope alive after signals from Earth stop.",
      synopsis: "Decades after Earth fell silent, the remaining crew of a deep-space habitat votes on whether to continue their orbit or venture into the unknown darkness.",
      cast: [
        { name: "Eli Mercer", role: "Captain", characterName: "Leo", imageUrl: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=500&q=80" }
      ],
      mediaGallery: [
        { type: "image", title: "Space habitat", src: "https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=1200&q=80" }
      ],
      streaming: [
        { name: "Netflix", status: "Streaming now" }
      ],
      reviews: [
        { author: "Astronut", type: "user", rating: 4, helpful: 64, text: "Quiet, scientific, and deeply human. Focuses on psychology rather than laser battles.", spoiler: false }
      ]
    },
    {
      id: "the-long-shadow",
      title: "The Long Shadow",
      type: "tv",
      tagline: "A dark procedural following a mystery in an isolated coal town.",
      year: 2025,
      runtime: "1 Season (8 eps)",
      genres: ["Thriller", "Drama"],
      language: "English",
      ageRating: "R",
      imdbRating: "8.4",
      cjRating: "8.9",
      popularityScore: "85%",
      director: "Daniel Quill",
      writers: "Sarah Chen",
      producers: "Lena Ortiz",
      productionCompany: "Aether Pictures",
      budget: "$20M",
      boxOffice: "N/A",
      country: "United States",
      languages: "English",
      awards: "Emmy Nominee — Best Drama Series",
      filmingLocations: "Pennsylvania",
      posterUrl: "https://images.unsplash.com/photo-1517602302552-471fe67acf66?auto=format&fit=crop&w=800&q=80",
      backdropUrl: "https://images.unsplash.com/photo-1517602302552-471fe67acf66?auto=format&fit=crop&w=1800&q=80",
      description: "An investigator returns to her rural hometown to solve a series of strange occurrences.",
      synopsis: "A gritty procedural set in a decaying rust-belt town, investigating how environmental secrets are tied to local government power dynamics.",
      cast: [
        { name: "Kiera Knight", role: "Lead Investigator", characterName: "Sarah", imageUrl: "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=500&q=80" }
      ],
      mediaGallery: [
        { type: "image", title: "Town Preview", src: "https://images.unsplash.com/photo-1517602302552-471fe67acf66?auto=format&fit=crop&w=1200&q=80" }
      ],
      streaming: [
        { name: "Prime Video", status: "Streaming now" }
      ],
      reviews: [
        { author: "CopFan", type: "user", rating: 4, helpful: 50, text: "Excellent atmosphere. Reminds me of True Detective season 1.", spoiler: false }
      ]
    },
    {
      id: "neon-gardens",
      title: "Neon Gardens",
      type: "anime",
      tagline: "In the cracks of a cybernetic metropolis, nature finds a way.",
      year: 2026,
      runtime: "12 Episodes",
      genres: ["Sci-Fi", "Anime", "Fantasy"],
      language: "Japanese",
      ageRating: "PG-13",
      imdbRating: "8.8",
      cjRating: "9.2",
      popularityScore: "93%",
      director: "Kenji Sato",
      writers: "Kenji Sato",
      producers: "Yuki Sato",
      productionCompany: "Studio Horizon",
      budget: "$8M",
      boxOffice: "N/A",
      country: "Japan",
      languages: "Japanese, English",
      awards: "Anime Awards Winner — Best Sci-Fi",
      filmingLocations: "Tokyo",
      posterUrl: "https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=800&q=80",
      backdropUrl: "https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=1800&q=80",
      description: "A botanist inside a giant dome city uncovers outlawed bio-engineering experiments.",
      synopsis: "In a fully synthetic dystopian dome, a rebel botanist uncovers genetically engineered plants that hold keys to restoration.",
      cast: [
        { name: "Aoi Yuki", role: "Voice Actress", characterName: "Iris", imageUrl: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=500&q=80" }
      ],
      mediaGallery: [
        { type: "image", title: "Still", src: "https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=1200&q=80" }
      ],
      streaming: [
        { name: "Crunchyroll", status: "Streaming now" }
      ],
      reviews: [
        { author: "AnimeWeeb", type: "user", rating: 5, helpful: 120, text: "A breath of fresh air for sci-fi anime. The world-building is top tier.", spoiler: false }
      ]
    }
  ];

  const mockCommunityPosts = [
    {
      id: "post-1",
      author: "Nadia L.",
      avatar: "N",
      time: "2h ago",
      movie: "The Last Horizon",
      movieSlug: "the-last-horizon",
      content: "Rewatched The Last Horizon today. That final scene at the shore gets me every single time. The way Mina Kade shoots the horizon as this unreachable line of hope... absolute masterclass in visual storytelling.",
      likes: 42,
      liked: false,
      comments: [
        { author: "Eli M.", text: "Fully agree. The score by Lina Ford during that scene is what sells it for me." }
      ]
    },
    {
      id: "post-2",
      author: "Jonah S.",
      avatar: "J",
      time: "5h ago",
      movie: "Kintsugi",
      movieSlug: "kintsugi",
      content: "Does anyone else feel Kintsugi is Hiroshi Ando's best work? The cinematography in Kanazawa is so quiet and deliberate, the composition mirrors the pottery repair. It's so elegant.",
      likes: 28,
      liked: false,
      comments: []
    },
    {
      id: "post-3",
      author: "The Film Ledger",
      avatar: "T",
      time: "1d ago",
      movie: "Glass Meridian",
      movieSlug: "glass-meridian",
      content: "Just posted our review of Glass Meridian. It's a precise, chilly, and highly focused map puzzle. Cillian Murphy's performance is quiet but intensely commanding.",
      likes: 56,
      liked: false,
      comments: [
        { author: "Marie C.", text: "It was a bit too slow in the second act, but the climax made up for it." }
      ]
    }
  ];

  const defaultUser = {
    name: "Alex Mercer",
    username: "alex_mercer",
    email: "alex.mercer@cinejunction.com",
    bio: "Cinemaphile, amateur photographer, and collector of physical screenprints. I prefer quiet, deliberate pacing and beautiful scores.",
    favoriteGenres: ["Sci-Fi", "Drama", "Mystery"],
    joinedAt: "2026-01-10T00:00:00Z",
    stats: {
      moviesWatched: 42,
      seriesWatched: 15,
      reviewsWritten: 8,
      watchTime: "112h"
    }
  };

  const trendingDiscussions = [
    "Is Sci-Fi the new dominant cinema genre?",
    "The Last Horizon ending: signal or memory?",
    "Best original scores of 2025/2026",
    "Hiroshi Ando retrospective: from early shorts to Kintsugi"
  ];

  const popularLists = [
    { title: "Quiet Melancholia", count: 12, author: "Camille D." },
    { title: "Cyberpunk & Organic Ruin", count: 8, author: "Kenji S." },
    { title: "Slow-burn procedurals", count: 15, author: "Tyler V." }
  ];

  const activeUsers = [
    { name: "Mina K.", initials: "MK", status: "online" },
    { name: "Eli M.", initials: "EM", status: "online" },
    { name: "Nadia L.", initials: "NL", status: "away" },
    { name: "Jonah S.", initials: "JS", status: "online" }
  ];

  window.CineJunction.mockData = {
    movies: mockMovies,
    communityPosts: mockCommunityPosts,
    defaultUser: defaultUser,
    trendingDiscussions: trendingDiscussions,
    popularLists: popularLists,
    activeUsers: activeUsers
  };
})();
