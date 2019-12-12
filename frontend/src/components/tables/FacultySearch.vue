<template>
    <div>
        <h2 class="m-4">Faculties table</h2>
        <div class="row w-100">
            <div class="col-md-4 offset-4">
                <b-table id="groups-table" bordered hover
                         fixed
                         head-variant="light"
                         :items="faculties"
                         :fields="fields"
                         :per-page="perPage"
                         :current-page="currentPage"
                         class="mb-2 align-self-center">
                </b-table>
            </div>
        </div>

        <div>
            <b-row class="w-100">
                <b-col>
                    <b-pagination
                            v-model="currentPage"
                            :total-rows="faculties.length"
                            :per-page="perPage"
                            aria-controls="faculties-table"
                            class="m-3 dark"
                    ></b-pagination>
                </b-col>
                <b-col>
                    <b-button v-b-modal.modal-adding-faculty class="m-3 float-right">Add</b-button>
                </b-col>
                <b-col>
                    <b-button v-on:click="getExportJson" class="m-3">
                        Export
                    </b-button>
                </b-col>
            </b-row>
            <b-form>
                <b-row class="w-100">
                    <b-col md="9">
                        <b-form-group label="Import:" label-for="file-default" label-cols-sm="2">
                            <b-form-file id="file-default" v-model="file">
                            </b-form-file>
                        </b-form-group>
                    </b-col>
                    <b-col md="3">
                        <b-button type="submit" variant="primary" v-on:click="submitFile">Import</b-button>
                    </b-col>
                </b-row>
            </b-form>
        </div>
        <div>
            <b-modal
                    id="modal-adding-faculty"
                    ref="modal"
                    centered
                    title="Add Faculty"
                    @show="resetModal"
                    @hidden="resetModal"
                    @ok="handleOk"
            >
                <form ref="form">
                    <b-form-group
                            :state="nameState"
                            label="name"
                            label-for="name-input"
                            invalid-feedback="Name is required"
                    >
                        <b-form-input
                                ref="name"
                                id="name-input"
                                v-model="name"
                                :state="nameState"
                                required
                        ></b-form-input>
                    </b-form-group>
                </form>
            </b-modal>
        </div>
    </div>
</template>

<script>
    export default {
        name: "FacultySearch",
        data (){
            return {
                perPage:10,
                currentPage: 1,
                fields: [
                    {
                        key: 'name',
                        label: 'Name'
                    }
                ],
                faculties: [],
                name: '',
                nameState: null,
                file: null
            }
        },
        mounted() {
            this.update();
        },
        methods: {
            checkFormValidity() {

                let validName = this.$refs.name.checkValidity();
                this.nameState = !!validName;

                return validName;
            },
            resetModal() {
                this.name = '';
                this.nameState = null;
            },
            handleOk(bvModalEvt) {
                // Prevent modal from closing
                bvModalEvt.preventDefault();

                if (!this.checkFormValidity()) {
                    return;
                }
                this.$client({
                    method: 'post',
                    url: '/add_faculty',
                    params: {
                        name: this.name
                    }
                })
                    .then(() => (this.update()))
                    .catch((er) => this.errorMessage = er.response.data)

                this.$nextTick(() => {
                    this.$refs.modal.hide();
                })
            },
            update() {
                this.$client
                    .get('/faculties')
                    .then(response => {
                        this.faculties = response.data;
                    });
            },
            getExportJson() {
                this.$client
                    .get('/export/faculties')
                    .then(response => {
                        // eslint-disable-next-line no-console
                        // console.log(response.data.toString())
                        this.download(response.data)
                    })
            },
            download(data) {
                let element = document.createElement('a');
                element.setAttribute('href', 'data:text/plain;charset=utf-8,' + JSON.stringify(data));
                element.setAttribute('download', 'faculties_export.json');

                element.style.display = 'none';
                document.body.appendChild(element);

                element.click();

                document.body.removeChild(element);
            },
            submitFile() {
                let fd = new FormData();
                fd.append('faculties', this.file);
                // eslint-disable-next-line no-console
                // console.log(fd)

                this.$client.post('/import/faculties', fd)
                    // .then(response => {
                        // eslint-disable-next-line no-console
                        // console.log(response.data)
                    // });
            }
        }
    }
</script>

<style scoped>

    h2 {
    }

</style>