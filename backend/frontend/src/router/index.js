import VueRouter from 'vue-router';
import IndexPage from "../components/IndexPage";
import StudentSearch from "../components/StudentSearch";
import GroupSearch from "../components/GroupSearch";

const routes = [
    {
        path: '',
        component: IndexPage
    },
    {
        path: '/students',
        component: StudentSearch
    },
    {
        path: '/groups',
        component: GroupSearch
    }
    // {
    //     path:'/statistics',
    //     // component: StatisticsPage
    // }
];

const router = new VueRouter({
    mode: 'history',
    routes
});

export default router;