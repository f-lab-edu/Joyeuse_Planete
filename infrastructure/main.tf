terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "eu-west-2"
}

data "aws_instance" "KAFKA_SERVER" {
  instance_id = "i-04cb31060b856b83e"
}

module "key_pair" {
  source = "./common/key"
}

module "securities" {
  source = "./common/security"
}

module "foods" {
  source                = "./foods"
  docker_food_image     = var.DOCKER_FOOD_IMAGE
  aws_security_group_id = module.securities.security_group_id
  aws_key_pair_name     = module.key_pair.aws_key_pair_name
  KAFKA_SERVER_IP       = data.aws_instance.KAFKA_SERVER.public_ip
}

module "orders" {
  source                = "./orders"
  docker_orders_image   = var.DOCKER_ORDERS_IMAGE
  aws_security_group_id = module.securities.security_group_id
  aws_key_pair_name     = module.key_pair.aws_key_pair_name
  KAFKA_SERVER_IP       = data.aws_instance.KAFKA_SERVER.public_ip
}

module "notifications" {
  source                     = "./notifications"
  docker_notifications_image = var.DOCKER_NOTIFICATIONS_IMAGE
  aws_security_group_id      = module.securities.security_group_id
  aws_key_pair_name          = module.key_pair.aws_key_pair_name
  KAFKA_SERVER_IP            = data.aws_instance.KAFKA_SERVER.public_ip
}

module "payment" {
  source                = "./payment"
  docker_payment_image  = var.DOCKER_PAYMENT_IMAGE
  aws_security_group_id = module.securities.security_group_id
  aws_key_pair_name     = module.key_pair.aws_key_pair_name
  KAFKA_SERVER_IP       = data.aws_instance.KAFKA_SERVER.public_ip
}

 module "api_gate_way" {
   source              = "./common/gateway"

   food_lb_ip          = module.foods.food_nginx_public_ip
   notifications_lb_ip = module.notifications.notifications_nginx_public_ip
   orders_lb_ip        = module.orders.orders_nginx_public_ip
   payments_lb_ip      = module.payment.payments_nginx_public_ip
 }