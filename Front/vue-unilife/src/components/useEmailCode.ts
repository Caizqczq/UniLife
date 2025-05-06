import request from "../../src/utils/request"


export function useEmailCode(){
    const sendEmailCode = async(email:string) =>
    {
        return await request.post('/user/code',
            {
                params:{email:email}
            }
        )
    }

    const verifyEmailCode = async(email:string,code:string) =>
    {
        return await request.post('users/login/code',
            {
                params:{email:email,code:code}
            }
        )
    }

    return{
        sendEmailCode,
        verifyEmailCode
    }
}
