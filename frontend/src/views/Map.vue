<template>
  <div class="mainView">
    <span id="map" class="mapView"> </span>
    <Slideout menu="#menu" side="right" easing="linear" panel="#panel" :padding="vw()" :toggleSelectors="['.toggle-button']">
      <nav id="menu" class="slideout-menu-width">
        <SideBar ref="SideBar" :oMap="oMap" :aMarkers="aMarkers"></SideBar>
      </nav>
      <main id="panel">
        <header>
          <div>
            <button class="toggle-button">☰</button>
          </div>
        </header>
      </main>
    </Slideout>
    <sweet-modal ref="introModal" @close="onIntroModalClose">{{ $t("map.intro") }}</sweet-modal>
  </div>
</template>

<script>
import Mapbox from "mapbox-gl";
import SideBar from "../components/SideBar";
import axios from "axios";
import Slideout from "vue-slideout";

export default {
  name: "Map",
  components: {
    SideBar,
    Slideout,
  },
  data() {
    return {
      sAccessToken: null,
      sMapStyle: "mapbox://styles/kai20/ckka3ohxp02fu17nsz37fitep",
      oMap: null,
      aMarkers: [],
    };
  },

  mounted() {
    axios.get(`/api/mapaccesstoken`).then((resp) => {
      this.sAccessToken = resp.data;
      this.$children[0].slideout.open();
      if (this.$cookies.get("cookies-agreed") === null) {
        this.$refs.introModal.open();
      } else {
        this.onIntroModalClose();
      }
    });
  },

  methods: {
    onIntroModalClose() {
      this.$cookies.set("cookies-agreed", true);

      this.mapbox = Mapbox;

      var maxBounds = [
        [-27.894531, 34.456636], // Southwest coordinates
        [52.009766, 72.01696], // Northeast coordinates
      ];

      Mapbox.accessToken = this.sAccessToken;
      this.oMap = new Mapbox.Map({
        container: "map",
        style: this.sMapStyle,
        center: [10, 53.53],
        zoom: 7,
        maxBounds: maxBounds,
      });

      this.oMap.on(
        "click",
        function (e) {
          this.$refs.SideBar.onClick(e);
        }.bind(this)
      );

      this.oMap.on(
        "moveend",
        function () {
          let endBounds = this.oMap.getBounds();
          this.loadMarkersForBounds(endBounds._ne.lng, endBounds._ne.lat, endBounds._sw.lng, endBounds._sw.lat);
        }.bind(this)
      );

      let bounds = this.oMap.getBounds();
      this.loadMarkersForBounds(bounds._ne.lng, bounds._ne.lat, bounds._sw.lng, bounds._sw.lat);
    },

    addMarker(marker) {
      marker.oMarker = new Mapbox.Marker().setLngLat([marker.longitude, marker.latitude]).addTo(this.oMap);

      marker.oMarker.getElement().dataset.dronespotId = marker.dronespotId;

      let vm = this;
      marker.oMarker.getElement().addEventListener("click", function () {
        vm.clickMarker(this);
      });
      marker.oMarker.getElement().addEventListener("touch", function () {
        vm.clickMarker(this);
      });
    },

    clickMarker(ele) {
      this.$refs.SideBar.showDronespot(ele.dataset.dronespotId);
      this.$refs.SideBar.aMarkers.forEach((marker) => {
        if (marker.oMarker._element.classList.contains("marker-marked")) {
          marker.oMarker._element.classList.remove("marker-marked");
        }
      });
      ele.classList.add("marker-marked");

      this.$children[0].slideout.open();
    },

    removeMarker(localMarker) {
      localMarker.oMarker.remove();
    },

    loadMarkersForBounds(nelng, nelat, swlng, swlat) {
      axios.get(`/api/markers/${nelng}/${nelat}/${swlng}/${swlat}`).then((resp) => {
        var aLoadMarkers = resp.data;

        //add new markers
        aLoadMarkers.forEach((loadMarker) => {
          var foundMarker = this.aMarkers.find((el) => el.dronespotId === loadMarker.dronespotId);
          if (foundMarker === undefined) {
            this.aMarkers.push(loadMarker);
            this.addMarker(loadMarker);
          }
        });

        //remove old markers
        this.aMarkers.forEach((localMarker) => {
          var foundMarker = aLoadMarkers.findIndex((el) => el.dronespotId === localMarker.dronespotId);
          if (foundMarker === -1) {
            this.aMarkers = this.aMarkers.filter(function (ele) {
              return ele.dronespotId != localMarker.dronespotId;
            });
            this.removeMarker(localMarker);
          }
        });
      });
    },

    vw() {
      var v = 0;
      var w = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);
      if (w <= 760) {
        v = 75;
      } else if (w > 760 && w <= 780) {
        v = 50;
      } else if (w > 780) {
        v = 25;
      }
      return (v * w) / 100;
    },
  },
};
</script>
