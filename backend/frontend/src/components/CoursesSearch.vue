<template>
    <div>
        <h2 class="m-4">Courses table</h2>
        <div class="row w-100">
            <div class="col-md-4 offset-4">
                <b-table id="courses-table" bordered hover
                         fixed
                         head-variant="light"
                         :items="courses"
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
                            :total-rows="courses.length"
                            :per-page="perPage"
                            aria-controls="courses-table"
                            class="m-3 dark"
                    ></b-pagination>
                </b-col>
                <b-col>
                    <b-button v-b-modal.modal-adding-course class="m-3 float-right">Add</b-button>
                </b-col>
            </b-row>
        </div>
        <div>
            <b-modal
                    id="modal-adding-course"
                    ref="modal"
                    centered
                    title="Add Course"
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
        name: "CourseSearch",
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
                courses: [],
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
                    url: '/add_course',
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
                    .get('/courses')
                    .then(response => {
                        this.courses = response.data;
                    });
            }
        }
    }
</script>

<style scoped>

    h2 {
    }

</style>