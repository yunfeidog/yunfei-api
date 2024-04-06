import Footer from '@/components/Footer';
import {
  AlipayCircleOutlined,
  LockOutlined,
  TaobaoCircleOutlined,
  UserOutlined,
  WeiboCircleOutlined,
} from '@ant-design/icons';
import {LoginForm, ProFormText,} from '@ant-design/pro-components';
import {history, Link} from '@umijs/max';
import {message, Tabs} from 'antd';
import React, {useState} from 'react';
import styles from './index.less';
import {userRegisterUsingPost} from '@/services/yunfeiapi-backend/userController';

const Login: React.FC = () => {
  const [type, setType] = useState<string>('account');
  const handleSubmit = async (values: API.UserLoginRequest) => {
    try {
      const res = await userRegisterUsingPost({
        ...values,
      });
      console.log("注册结果：", res)
      if (res.data) {
        message.success('注册成功！');
        history.push('/user/login');
        return;
      }
    } catch (error) {
      const defaultLoginFailureMessage = '注册失败，请重试！';
      console.error(error);
      message.error(defaultLoginFailureMessage);
    }
  };
  return (
    <div className={styles.container}>
      <div className={styles.content}>
        <LoginForm
          submitter={{
            searchConfig: {
              submitText: '注册',
            },
          }}
          logo={<img alt="logo" src="/logo.svg"/>}
          title="云飞接口"
          subTitle={'API 开放平台'}
          initialValues={{
            autoLogin: true,
          }}
          actions={[
            '其他注册方式 :',
            <AlipayCircleOutlined key="AlipayCircleOutlined" className={styles.icon}/>,
            <TaobaoCircleOutlined key="TaobaoCircleOutlined" className={styles.icon}/>,
            <WeiboCircleOutlined key="WeiboCircleOutlined" className={styles.icon}/>,
          ]}
          onFinish={async (values) => {
            await handleSubmit(values as API.UserRegisterRequest);
          }}
        >
          <Tabs
            activeKey={type}
            onChange={setType}
            centered
            items={[
              {
                key: 'account',
                label: '账户密码注册',
              },
            ]}
          />

          {type === 'account' && (
            <>
              <ProFormText
                name="userAccount"
                key="userAccount"
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined className={styles.prefixIcon}/>,
                }}
                placeholder={'请输入用户名'}
                rules={[
                  {
                    required: true,
                    message: '用户名是必填项！',
                  },
                ]}
              />
              <ProFormText.Password
                key="userPassword"
                name="userPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined className={styles.prefixIcon}/>,
                }}
                placeholder={'请输入密码'}
                rules={[
                  {
                    required: true,
                    message: '密码是必填项！',
                  },
                ]}
              />
              <ProFormText.Password
                name="checkPassword"
                key="checkPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined className={styles.prefixIcon}/>,
                }}
                placeholder={'请再次输入密码'}
                rules={[
                  {
                    required: true,
                    message: '确认密码是必填项！',
                  },
                ]}
              />
            </>
          )}
          <div
            style={{
              marginBottom: 24,
            }}
          >
            <Link
              style={{
                float: 'right',
                marginBottom: 14,
              }}
              to="/user/login"
            >
              已有账号 ?
            </Link>
          </div>
        </LoginForm>
      </div>
      <Footer/>
    </div>
  );
};
export default Login;
