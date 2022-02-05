<template>
  <div class="sideBar" id="sideBar">
    <h3>{{ $t("main.title") }}</h3>
    <hr class="titleRule" />
    <div class="sidebarContent">
      <div class="tab-container tabs-fill">
        <ul>
          <li v-bind:class="[activeTab == 0 ? 'selected' : '']">
            <div class="tab-item-content" @click="activeTab = 0">
              {{ $t("about.title") }}
            </div>
          </li>
          <li v-bind:class="[activeTab == 1 ? 'selected' : '']">
            <div class="tab-item-content" @click="activeTab = 1">
              {{ $t("spot.title") }}
            </div>
          </li>
          <li v-bind:class="[activeTab == 2 ? 'selected' : '']">
            <div class="tab-item-content" @click="activeTab = 2">
              {{ $t("add.title") }}
            </div>
          </li>
          <li v-bind:class="[activeTab == 3 ? 'selected' : '']">
            <div class="tab-item-content" @click="activeTab = 3">
              {{ $t("login.title") }}
            </div>
          </li>
        </ul>
      </div>
    </div>
    <br />
    <div v-if="activeTab == 0">
      <p v-html="$t('about.text')"></p>
    </div>
    <div v-if="activeTab == 1">
      <div class="row level" v-if="selectedMarker">
        <div class="col-12 level-item">
          <img
            v-img
            :src="selectedMarker.imagePath"
            v-if="selectedMarker.imagePath"
            class="spotImage"
            alt="spotImage"
          />
        </div>
        <div class="col-12 level-item">
          <p>{{ selectedMarker.description }}</p>
        </div>
        <table style="width:100%">
          <tr>
            <td>
              <b>{{ $t("add.lat") }}</b>
            </td>
            <td>{{ selectedMarker.latitude }}</td>
          </tr>

          <tr>
            <td>
              <b>{{ $t("add.lng") }}</b>
            </td>
            <td>{{ selectedMarker.longitude }}</td>
          </tr>
          <tr>
            <td>
              <b>{{ $t("add.foottraffic.title") }}</b>
            </td>
            <td>{{ $t("add.foottraffic." + selectedMarker.footTraffic) }}</td>
          </tr>
          <tr v-if="selectedMarker.authorDisplayName">
            <td>
              <b>{{ $t("add.createdBy") }}</b>
            </td>
            <td>@{{ selectedMarker.authorDisplayName }}</td>
          </tr>
        </table>

        <br />
        <a
          :href="
            concatNavLink(selectedMarker.latitude, selectedMarker.longitude)
          "
          target="_blank"
          class="minFullWidth navigateButton"
          ><button>{{ $t("spot.navigate") }}</button></a
        >
      </div>
      <div v-if="!selectedMarker">
        <p>{{ $t("spot.placeholder") }}</p>
      </div>
    </div>
    <div v-if="activeTab == 2">
      <vue-element-loading :active="submitIsLoading" spinner="bar-fade-scale" />
      <div class="row level" v-if="isSignIn">
        <div class="col-12 ignore-screen level-item">
          <p class="m-0">{{ $t("add.image") }}:</p>
        </div>
        <div class="col-12 level-item">
          <PictureInput
            ref="pictureInput"
            type="file"
            accept="image/jpeg,image/png"
            size="10"
            :width="picInput.w"
            :height="picInput.h"
            @change="onFileSelect"
          ></PictureInput>
        </div>

        <div class="col-12 ignore-screen level-item">
          <p class="m-0">{{ $t("add.position") }}:</p>
        </div>
        <div class="col-12 ignore-screen level-item">
          <v-btn color="primary" @click="listeningForClickCoordinates = true">{{
            $t("add.pick")
          }}</v-btn>
        </div>
        <div class="col-6 level-item">{{ $t("add.lat") }}</div>
        <div class="col-6 level-item">{{ $t("add.lng") }}</div>
        <div class="col-6 level-item">
          <v-input-field
            placeholder="Latitude"
            v-model="formData.lat"
          ></v-input-field>
        </div>
        <div class="col-6 level-item">
          <v-input-field
            placeholder="Longitude"
            v-model="formData.lng"
          ></v-input-field>
        </div>

        <div class="col-12 ignore-screen level-item">
          <p class="m-0">{{ $t("add.description") }}:</p>
        </div>
        <div class="col-12 level-item">
          <textarea
            :placeholder="$t('add.descriptionLong')"
            v-model="formData.description"
          >
          </textarea>
        </div>

        <div class="col-12 ignore-screen level-item">
          <p class="m-0">{{ $t("add.foottraffic.title") }}:</p>
        </div>
        <div class="col-12 level-item">
          <div class="input-control minFullWidth">
            <select
              class="select"
              placeholder="Select One"
              v-model="formData.footTraffic"
              ><option value="0">{{ $t("add.foottraffic.0") }}</option
              ><option value="1">{{ $t("add.foottraffic.1") }}</option>
              <option value="2">{{ $t("add.foottraffic.2") }}</option>
            </select>
          </div>
        </div>

        <div
          class="col-12 ignore-screen level-item form-ext-control form-ext-checkbox"
        >
          <input
            id="chkAnonymous"
            class="form-ext-input"
            type="checkbox"
            v-model="formData.chkAnonymous"
          />
          <label class="form-ext-label" for="chkAnonymous"
            ><small>{{ $t("add.anonymous") }}</small></label
          >
        </div>

        <div
          class="col-12 ignore-screen level-item form-ext-control form-ext-checkbox"
        >
          <input
            id="chkLegal"
            class="form-ext-input"
            type="checkbox"
            v-model="formData.chkLegal"
          />
          <label class="form-ext-label" for="chkLegal"
            ><small>{{ $t("add.legal") }}</small></label
          >
        </div>

        <div class="col-12 ignore-screen level-item">
          <v-btn
            color="info"
            @click="onSubmit"
            :disabled="!formData.chkLegal"
            >{{ $t("add.submit") }}</v-btn
          >
        </div>

        <div></div>
      </div>
      <div v-if="!isSignIn">
        <p>{{ $t("add.loginFirst") }}</p>
      </div>
    </div>
    <div v-if="activeTab == 3">
      <button
        class="btn-info"
        @click="handleClickSignIn"
        v-if="!isSignIn && !isNotRegistered"
      >
        {{ $t("login.buttonGoogle") }}
      </button>
      <div v-if="isNotRegistered">
        <div class="col-12 ignore-screen level-item">
          <p class="m-0">{{ $t("login.email") }}:</p>
        </div>
        <div class="col-12 level-item">
          <input v-model="oAuthUser.email" disabled="true" />
        </div>
        <div class="col-12 ignore-screen level-item">
          <p class="m-0">{{ $t("login.displayname") }}:</p>
        </div>
        <div class="col-12 level-item">
          <input v-model="oAuthUser.displayname" />
        </div>
        <br />
        <div class="col-12 ignore-screen level-item">
          <v-btn color="info" @click="onRegister">{{
            $t("login.register")
          }}</v-btn>
        </div>
      </div>
      <div v-if="oAuthUser.isRegistered == true && isSignIn == true">
        <p>
          {{ $t("login.loggedIn", { displayname: oAuthUser.displayname }) }}
        </p>
        <p>{{ $t("login.explaination") }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import PictureInput from "./PictureInput";
import Mapbox from "mapbox-gl";
import axios from "axios";

export default {
  name: "SideBar",
  props: ["oMap", "aMarkers"],
  components: {
    PictureInput,
  },
  data: function() {
    return {
      activeTab: 0,
      listeningForClickCoordinates: false,
      oTempMarker: null,
      formData: {
        lng: 0,
        lat: 0,
        description: "",
        footTraffic: 1,
        chkLegal: false,
        chkAnonymous: false,
      },
      picInput: {
        w: 0,
        h: 0,
      },
      selectedMarker: null,
      isInit: false,
      isSignIn: false,
      isNotRegistered: false,
      oAuthUser: {
        idToken: null,
        email: null,
        displayname: null,
        isRegistered: null,
        userId: null,
      },
      submitIsLoading: false,
    };
  },
  mounted() {
    this.picInput.w = this.vw(25);
    this.picInput.h = (this.picInput.w * 9) / 16;
  },
  methods: {
    onClick(e) {
      if (this.listeningForClickCoordinates) {
        this.formData.lng = e.lngLat.lng;
        this.formData.lat = e.lngLat.lat;
        this.listeningForClickCoordinates = false;

        this.addMarkerAndFly();
      }
    },

    onFileSelect(img) {
      this.formData = {
        lng: 0,
        lat: 0,
        description: "",
        footTraffic: 1,
      };

      if (img) {
        let vm = this;
        //eslint-disable-next-line no-undef
        EXIF.getData(this.$refs.pictureInput.file, function() {
          var lngSuffix = this.exifdata.GPSLongitude[2];
          var latSuffix = this.exifdata.GPSLatitude[2];

          vm.formData.lng =
            1 *
            (this.exifdata.GPSLongitude[0] +
              this.exifdata.GPSLongitude[1] / 60 +
              lngSuffix / 3600);
          vm.formData.lat =
            1 *
            (this.exifdata.GPSLatitude[0] +
              this.exifdata.GPSLatitude[1] / 60 +
              latSuffix / 3600);

          if (vm.formData.lng != 0 && vm.formData.lat != 0) {
            vm.addMarkerAndFly();
          }
        });
      }
    },

    addMarkerAndFly() {
      if (this.oTempMarker) {
        this.oTempMarker.remove();
      }

      this.oTempMarker = new Mapbox.Marker()
        .setLngLat([this.formData.lng, this.formData.lat])
        .addTo(this.oMap);

      this.oMap.flyTo({
        center: [this.formData.lng, this.formData.lat],
        zoom: 14,
        essential: true,
      });
    },

    onSubmit() {
      var formData = new FormData();

      if (!this.formData.chkAnonymous) {
        this.formData.chkAnonymous = false;
      }

      var oFormBody = {
        latitude: this.formData.lat,
        longitude: this.formData.lng,
        description: this.formData.description,
        footTraffic: this.formData.footTraffic,
        isAnonymous: this.formData.chkAnonymous,
      };
      formData.append("formBody", JSON.stringify(oFormBody));
      formData.append("imageBlob", this.$refs.pictureInput.file);

      this.submitIsLoading = true;

      axios
        .post("/api/dronespot", formData, {
          "Content-Type": undefined,
        })
        .then(() => {
          this.$notify({
            group: "notifications",
            type: "success",
            position: "top center",
            title: this.getMessage("messages.success"),
            text: this.getMessage("messages.submittedDronespots"),
          });
          this.submitIsLoading = false;
        })
        .catch((error) => {
          this.submitIsLoading = false;
          if (error.response.status == 403 || error.response.status == 400) {
            this.$notify({
              group: "notifications",
              type: "error",
              position: "top center",
              title: this.getMessage("messages.error"),
              text: this.getMessage("messages.notsignedin"),
            });
          } else if (error.response.status == 416) {
            this.$notify({
              group: "notifications",
              type: "error",
              position: "top center",
              title: this.getMessage("messages.error"),
              text: this.getMessage("messages.isNoImage"),
            });
          } else if (error.response.status == 409) {
            this.$notify({
              group: "notifications",
              type: "error",
              position: "top center",
              title: this.getMessage("messages.error"),
              text: this.getMessage("messages.tooClose"),
            });
          }
        });
    },

    showDronespot(dronespotId) {
      this.selectedMarker = this.aMarkers.find(
        (el) => el.dronespotId === Number(dronespotId)
      );
      this.activeTab = 1;
    },

    vw(v) {
      var w = Math.max(
        document.documentElement.clientWidth,
        window.innerWidth || 0
      );
      return (v * w) / 100;
    },

    concatNavLink(lat, lng) {
      return `https://www.google.com/maps/dir//${lat},${lng}/@${lat},${lng},17z/data=!4m2!4m1!3e0`;
    },

    handleClickSignIn() {
      this.$gAuth
        .getAuthCode()
        .then((authCode) => {
          return axios.post("/api/auth/google", authCode);
        })
        .then((response) => {
          this.oAuthUser = response.data;

          axios.defaults.headers.common["idToken"] = this.oAuthUser.idToken;

          if (this.oAuthUser.isRegistered) {
            this.isSignIn = true;
          } else {
            this.isNotRegistered = true;
          }
        })
        .catch((error) => {
          if (error.response.status == 409) {
            this.$notify({
              group: "notifications",
              type: "error",
              position: "top center",
              title: this.getMessage("messages.error"),
              text: this.getMessage("messages.emailalreadyregistered"),
            });
          } else if (
            error.response.status == 403 ||
            error.response.status == 400
          ) {
            this.$notify({
              group: "notifications",
              type: "error",
              position: "top center",
              title: this.getMessage("messages.error"),
              text: this.getMessage("messages.notsignedin"),
            });
          }
          console.log(error);
        });
    },

    onRegister() {
      axios.put("/api/auth/register", this.oAuthUser).then(function() {
        this.$notify({
          group: "notifications",
          type: "success",
          position: "top center",
          title: this.getMessage("messages.success"),
          text: this.getMessage("messages.registrationsuccessful"),
        });
        this.isSignIn = true;
      });
    },

    getMessage: function(key) {
      return this.$t(key);
    },
  },
};
</script>

<style scoped></style>
