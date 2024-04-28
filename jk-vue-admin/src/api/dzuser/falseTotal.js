import request from '@/utils/request'
export default {
    getList:function(params) {
        return request({
            url: '/dzuser/falsetotal/list',
            method: 'get',
            params
        })
    },
    add:function(params) {
        return request({
            url: '/dzuser/falsetotal',
            method: 'post',
            data: params
        })
    },
    update:function(params) {
        return request({
            url: '/dzuser/falsetotal',
            method: 'PUT',
            data: params
        })
    },
    remove:function(id) {
        return request({
            url: '/dzuser/falsetotal',
            method: 'delete',
            params: {
                id: id
            }
        })
    }
}
