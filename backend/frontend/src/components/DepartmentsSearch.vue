<template>
    <div>
        <h2 class="m-4">Departments table</h2>
        <div class="row w-100">
            <div class="col-md-4 offset-4">
                <b-table id="groups-table" bordered hover
                         fixed
                         head-variant="light"
                         :items="departments"
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
                            :total-rows="departments.length"
                            :per-page="perPage"
                            aria-controls="departments-table"
                            class="m-3 dark"
                    ></b-pagination>
                </b-col>
                <b-col>
                    <b-button v-b-modal.modal-adding-department class="m-3 float-right">Add</b-button>
                </b-col>
            </b-row>
        </div>
        <div>
            <b-modal
                    id="modal-adding-department"
                    ref="modal"
                    centered
                    title="Add Department"
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
        name: "DepartmentSearch",
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
                departments: [],
                name: '',
                nameState: null
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
                    url: '/add_department',
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
                    .get('/departments')
                    .then(response => {
                        this.departments = response.data;
                    });
            }
        }
    }
</script>

<style scoped>

    h2 {
    }

</style>