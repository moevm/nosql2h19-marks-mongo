<template>
    <div>
        <h2 class="m-4">Semesters table</h2>
        <div class="row w-100">
            <div class="col-md-4 offset-4">
                <b-table id="semesters-table" bordered hover
                         fixed
                         head-variant="light"
                         :items="semesters"
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
                            :total-rows="semesters.length"
                            :per-page="perPage"
                            aria-controls="semesters-table"
                            class="m-3 dark"
                    ></b-pagination>
                </b-col>
                <b-col>
                    <b-button v-b-modal.modal-adding-semester class="m-3 float-right">Add</b-button>
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
                    id="modal-adding-semester"
                    ref="modal"
                    centered
                    title="Add Semester"
                    @show="resetModal"
                    @hidden="resetModal"
                    @ok="handleOk"
            >
                <form ref="form">
                    <b-form-group
                            :state="yearState"
                            label="year"
                            label-for="year-input"
                            invalid-feedback="Year is required"
                    >
                        <b-form-input
                                ref="year"
                                id="year-input"
                                v-model="year"
                                :state="yearState"
                                required
                        ></b-form-input>
                    </b-form-group>
                    <b-form-group
                            :state="periodState"
                            label="period"
                            label-for="period-input"
                            invalid-feedback="Period is required"
                    >
                        <b-form-input
                                ref="period"
                                id="period-input"
                                v-model="period"
                                :state="periodState"
                                required
                        ></b-form-input>
                    </b-form-group>
                </form>
                <div class="err">
                    {{errorMessage}}
                </div>
            </b-modal>
        </div>
    </div>
</template>

<script>
    export default {
        name: "SemesterSearch",
        data (){
            return {
                perPage:10,
                currentPage: 1,
                fields: [
                    {
                        key: 'year',
                        label: 'Year'
                    },
                    {
                        key: 'period',
                        label: 'Period'
                    }
                ],
                semesters: [],
                year: '',
                period: '',
                yearState: null,
                periodState: null,
                errorMessage: null,
                file:null
            }
        },
        mounted() {
            this.update();
        },
        methods: {
            checkFormValidity() {

                let validYear = this.$refs.year.checkValidity();
                if (!Number(this.year))
                    validYear = false;
                this.yearState = validYear;

                let validPeriod = this.$refs.period.checkValidity();
                if (this.period != 'spring' && this.period != 'autumn') {
                    validPeriod = false;
                }
                this.periodState = validPeriod;

                return validYear && validPeriod;
            },
            resetModal() {
                this.year = '';
                this.period = '';
                this.yearState = null;
                this.periodState = null;
                this.errorMessage = null;
            },
            handleOk(bvModalEvt) {
                // Prevent modal from closing
                bvModalEvt.preventDefault();
                this.errorMessage = null;

                if (!this.checkFormValidity()) {
                    return;
                }

                this.$client({
                    method: 'post',
                    url: '/add_semester',
                    params: {
                        year: this.year,
                        period: this.period,
                    }
                })
                    .then(() => (this.update()))
                    .catch((er) => this.errorMessage = er.response.data)

                if(!this.errorMessage){
                    return;
                }

                this.$nextTick(() => {
                    this.$refs.modal.hide();
                })
            },
            update() {
                this.$client
                    .get('/semesters')
                    .then(response => {
                        this.semesters = response.data;
                    });
            },
            getExportJson() {
                this.$client
                    .get('/export/semesters')
                    .then(response => {
                        // eslint-disable-next-line no-console
                        // console.log(response.data.toString())
                        this.download(response.data)
                    })
            },
            download(data) {
                let element = document.createElement('a');
                element.setAttribute('href', 'data:text/plain;charset=utf-8,' + JSON.stringify(data));
                element.setAttribute('download', 'semesters_export.json');

                element.style.display = 'none';
                document.body.appendChild(element);

                element.click();

                document.body.removeChild(element);
            },
            submitFile() {
                let fd = new FormData();
                fd.append('semesters', this.file);
                // eslint-disable-next-line no-console
                // console.log(fd)

                this.$client.post('/import/semesters', fd)
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
    .err{
        color: darkred;
    }

</style>